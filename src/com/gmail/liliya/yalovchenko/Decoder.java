package com.gmail.liliya.yalovchenko;


/**
 *<p>The class {@code Decoder} contains method for performing
 * parsing operation of encoded string to csv format.</p>
 *
 * <p>Decoder has StateMachine which process string char by char,
 * passing each char to predefined State.</p>
 * <p>Methods in Decoder are static, so you can use it without initializing
 * instance of Decoder class. After returning result of parsing all static
 * fields of Decoder are resent to initial value.</p>
 *
 * @author  Liliya Yalovchenko
 * **/
public final class Decoder {

    /**
     * Don't let anyone instantiate this class.
     */
    private Decoder() {}

    /**
     * The {@code int} value that counts index of
     * char that is currently parsing
     */
    static int index = 0;

    /**
     * The {@code int} value that counts length of driver code
     * MAX_VALUE = 4
     */
    static int driverCodeCounter = 0;

    /**
     * The {@code int} value that counts length of temperature code
     */
    static  int temperatureCounter = 0;

    /**
     * The {@code int} value that remember index
     * where wrong char in input string located
     */
    static int indexOfError = 0;

    /**
     * The {@code char} value that remember wrong char in input string
     */
    static char invalidChar = ' ';

    /**
     * The {@code int} constant value that is the length of driver code
     */
    static final int DC = 4;

    /**
     * The {@code char} constant value that is code of danger cargo
     */
    static final char D = 'd';

    /**
     * The {@code char} constant value that is code of frowy cargo
     */
    static final char F = 'f';

    /**
     * The {@code int} constant value that is length of trip track number (LoTTN1 = 4 or LoTTN2 = 3 - two variants)
     */
    static final int LoTTN1 = 4;
    static final int LoTTN2 = 3;

    /**
     * The {@code int} constant value that is temperature format length
     */
    static final int T = 4;

    /**
     * The {@code int} constant value that is amount of non-significant character in string code
     */
    static final int NS = 2;



    /**
     * Decode input string to csv line
     * Append additional space character to the end of string,
     * which will define the valid end of parsing
     *
     * @return StringBuilder line in csv format
     * **/
    public static StringBuilder decode(String s) {
        StringBuilder result = new StringBuilder();


        if (isNotEmpty(s)) {
            StateAutomate sm = new StateAutomate();
            String codeToParse = s.concat(" ");
            sm.pushWholeStringCode(s);

            for (char c : codeToParse.toCharArray()) {
                sm.next(c);
            }
            result = sm.getResult();
            resetStaticCounters(sm);
        } else {
            result.append("Empty string");
        }

        return result;
    }

    /**
     * After returning result of parsing all static
     * fields and counters of Decoder are resent to initial value in order to decode next line
     */
    private static void resetStaticCounters(StateAutomate sm) {
        sm.state = StateAutomate.State.DRIVER_CODE;
        index = 0;
        driverCodeCounter = 0;
        temperatureCounter = 0;
        indexOfError = 0;
        invalidChar = ' ';
    }

    private static boolean isNotEmpty(String s) {
        return s != null && s.length() > 0;
    }

    /**
     * Static nested class, contains State enum.
     * State process the string char by char,
     * passing each char to predefined State
     *
     * According to specification there are 9 State. They are the following:
     * DRIVER_CODE
     * TRIP_TICKET_CODE
     * NON_SIGNIFICANT_CHAR
     * TRY_FIND_DRIVER_CODE_IN_TRIP_TRACK
     * DRIVER_CODE_IN_TRIP_TRACK
     * TEMPERATURE
     * NAME
     * VALID_END
     * INVALID_END
     */
    private static class StateAutomate {

        private State state = State.DRIVER_CODE;
        private ParserData data = new ParserData();


        /**
         * Pass each char to State and increase index
         * @param   ch   symbol in input code String.
         */
        public void next(char ch) {
            state = state.next(ch, data);
            index++;
        }

        /**
         * Put input string in csv file as first value in row
         */
        public void pushWholeStringCode(String s) {
            data.addFirstElement(s);
        }

        /**
         * Returns a result of parsing
         * Special cases:
         * <ul><li>If the last state = INVALID_END then the result is "Wrong input format at index"
         * with wrong char and index
         * <li>If state = VALID_END, then the result is a string in csv format
         * <li>In any other case - "Something went wrong while parsing"
         * * </ul>
         *
         * @return  {@code StringBuilder} csv row.
         */
        public StringBuilder getResult() {

            return this.state == State.INVALID_END ?
                    new StringBuilder("Wrong input format at index " + indexOfError + " char = \"" + invalidChar + "\"") :
                    (this.state == State.VALID_END ? data.getResult() : new StringBuilder("Something went wrong while parsing"));
        }

        /**Enumeration of State that parsing encode string**/
        private enum State {

            /**
             * Analise first four chars;
             * can go to DRIVER_CODE, TRIP_TICKET_CODE, INVALID_END or NON_SIGNIFICANT_CHAR
             */
            DRIVER_CODE {
                @Override
                public State next(char ch, ParserData data) {
                    State result;

                    if (index <= DC - 1)  {
                        result = handleDriverCode(ch, data);
                    } else if ((index == DC) && DecoderUtil.isR(ch)) {
                        result = handleTripTicketCode(ch, data);
                    } else if ((index == DC) && !DecoderUtil.isR(ch)) {
                        result = NON_SIGNIFICANT_CHAR;
                    } else {
                        result = INVALID_END;
                        rememberInvalidPlaceInString(ch);
                    }
                    return result;
                }
            },

            /**
             * Parse 2 non-significant symbols
             * <ul>
             *     <li>If char is not R or t and index is greater than length of driver code - NON_SIGNIFICANT_CHAR</li>
             *     <li>If char is  R or t - TRIP_TICKET_CODE</li>
             *     <li>If else - INVALID_END</li>
             * </ul>
             */
            NON_SIGNIFICANT_CHAR {
                @Override
                public State next(char ch, ParserData data) {

                    if (!DecoderUtil.isR(ch) && (index == (DC + 1))) {
                        return NON_SIGNIFICANT_CHAR;
                    } if (DecoderUtil.isR(ch)) {
                        return handleTripTicketCode(ch, data);
                    } else {
                        rememberInvalidPlaceInString(ch);
                        return INVALID_END;
                    }
                }
            },

            /**
             * Parse trip track code
             * <ul>
             *     <li>If char is not R or t and index equals to length of driver code - INVALID_END</li>
             *     <li>Else - TRIP_TICKET_CODE</li>
             * </ul>
             */
            TRIP_TICKET_CODE {
                @Override
                public State next(char ch, ParserData data) {
                    State result;

                    if (index == DC && !DecoderUtil.isR(ch)) {
                        rememberInvalidPlaceInString(ch);
                        result = INVALID_END;
                    } else {
                        result = handleTripTicketCode(ch, data);
                    }
                    return result;
                }
            },
            /**
             * Define whether there is driver code in trip track code
             * <ul>
             *     <li>If char is not R or t and index equals to length of driver code - INVALID_END</li>
             *     <li>Else - TRIP_TICKET_CODE</li>
             * </ul>
             */
            TRY_FIND_DRIVER_CODE_IN_TRIP_TRACK {
                @Override
                public State next(char ch, ParserData data) {
                    State result;

                    if (DecoderUtil.isSignOfTemp(ch)) {
                        result = handleTemperatureSign(ch, data);
                    } else if (ch == data.getDriverCode().charAt(driverCodeCounter++)) {
                        result = handleAgainDriverCode(ch, data);
                    } else if (DecoderUtil.isDigit(ch)) {
                        result = handleName(ch, data);
                    } else {
                        rememberInvalidPlaceInString(ch);
                        result = INVALID_END;
                    }
                    return result;
                }
            },
            /**
             * Parse driver code in trip track code
             *
             * <ul>
             *     <li>If driver code is not equal to the
             *     driver code in the beginning of the sting - INVALID_END</li>
             *     <li>If char is not + or - and index is 4 - TEMPERATURE</li>
             *     <li>If char is number and index is 4 - NAME</li>
             *     <li>Else - DRIVER_CODE_IN_TRIP_TRACK</li>
             * </ul>
             */
            DRIVER_CODE_IN_TRIP_TRACK {
                @Override
                public State next(char ch, ParserData data) {
                    State result;

                    if ((DecoderUtil.isSignOfTemp(ch)) && driverCodeCounter == DC) {
                        result = handleTemperatureSign(ch, data);

                    } else if (driverCodeCounter < DC) {

                        if (ch == data.getDriverCode().charAt(driverCodeCounter++)) {
                            result = handleAgainDriverCode(ch, data);
                        } else {
                            rememberInvalidPlaceInString(ch);
                            result = INVALID_END;
                        }
                    } else if (DecoderUtil.isDigit(ch) && driverCodeCounter == DC) {
                        result = handleName(ch, data);
                    } else {
                        rememberInvalidPlaceInString(ch);
                        result = INVALID_END;
                    }
                    return result;
                }
            },
            /**
             * Handle string by 3 chars, each of 3 char is octal number,
             * that converted to decimal and then to char in ASCII.
             * Can go to NAME or VALID_END.
             *
             * <ul>
             *     <li>If temperatureCounter < temperature length - TEMPERATURE</li>
             *     <li>If temperatureCounter = temperature length and ch is number - NAME</li>
             *     <li>Else - INVALID_END</li>
             * </ul>
             */
            TEMPERATURE {
                @Override
                public State next(char ch, ParserData data) {
                    State result;

                    if (T > ++temperatureCounter) {
                        result = handleTemperature(ch, data);
                    } else if (temperatureCounter == T && DecoderUtil.isDigit(ch)) {
                        result = handleName(ch, data);
                    } else {
                        rememberInvalidPlaceInString(ch);
                        result = INVALID_END;
                    }
                    return result;
                }
            },
            /**
             * Parse chars that represents octal numbers
             *
             * <ul>
             *     <li>If char = ' ' - VALID_END</li>
             *     <li>If char is number - NAME</li>
             * </ul>
             */
            NAME {
                @Override
                public State next(char ch, ParserData data) {
                    return handleName(ch, data);
                }
            },

            /**
             * Indicates about valid end of parsing string
             */
            VALID_END {
                @Override
                public State next(char c, ParserData data) {
                    return VALID_END;
                }
            },
            /**
             * When StateMachine encounters wrong character
             * which is not match encoding format it returns INVALID_END,
             * wrong char and index of it. It is wrote to csv file also
             */
            INVALID_END {
                @Override
                public State next(char ch, ParserData data) {
                    return INVALID_END;
                }
            };

            /**
             * Parse char and return State to handle next char
             *
             * @return StateMachine.State
             */
            public abstract State next(char ch, ParserData data);

            /**
             * Append current character to {@code driverCode} field in {@code ParserData}
             * @param   ch   current character
             * @param   data   ParserData
             * @return  next State of StateMachine {@code State.DRIVER_CODE}
             */
            private static State handleDriverCode(char ch, ParserData data) {
                data.pushDriverCode(ch);
                return DRIVER_CODE;
            }

            /**
             * Append current character to {@code driverCodeAgain} field in {@code ParserData}
             * @param   ch   current character
             * @param   data   ParserData
             * @return  next State of StateMachine {@code State.DRIVER_CODE_IN_TRIP_TRACK}
             */
            private static State handleAgainDriverCode(char ch, ParserData data) {
                data.pushDriverCodeAgain(ch);
                return DRIVER_CODE_IN_TRIP_TRACK;
            }

            /**
             * Handle chars in State.RIP_TICKET_CODE
             * <ul>
             *     <li>If {@code ch} is R or r and {@code index} is less then driver code
             *      length + number of non-significant chars - append current character
             *      to {@code tripTrackCode} field in {@code ParserData}</li>
             *      <li>If {@code ch} = 'd' and {@code ParserData} is not yet danger - invoke {@code ParserDate} setDanger()</li>
             *      <li>If {@code ch} = 'f' and {@code ParserDate} is not yet frowy - invoke {@code ParserDate} setFrowy()</li>
             *      <li>If {@code ch} is number - append next 3 or 4 chars to {@code tripTrackCode} field in {@code ParserData}</li>
             *      <li>When trip track code is parsed  - {@code StateMachine.State} = TRY_FIND_DRIVER_CODE_IN_TRIP_TRACK</li>
             * </ul>
             * @param   ch   current character
             * @param   data   ParserData
             * @return  next State of StateMachine {@code State.TRIP_TICKET_CODE},
             * {@code State.TRY_FIND_DRIVER_CODE_IN_TRIP_TRACK} or {@code State.INVALID_END}
             */
            private static State handleTripTicketCode(char ch, ParserData data) {

                if (DecoderUtil.isR(ch) && (index >= DC && index <= (DC + NS) )) {
                    data.pushTripTrackCode(ch);
                    return TRIP_TICKET_CODE;
                } else if (ch == D && (!data.isDanger())) {
                    data.pushTripTrackCode(ch);
                    data.setDanger();
                    return TRIP_TICKET_CODE;
                } else if (ch == F && (!data.isFrowy())) {
                    data.pushTripTrackCode(ch);
                    data.setFrowy();
                    return TRIP_TICKET_CODE;
                } else if (DecoderUtil.isDigit(ch)) {

                    if (!data.hasLengthOfTripTrackNumber()) {
                        int lengthOfTripNumber = (data.isDanger() || data.isFrowy()) ? LoTTN2 : LoTTN1;
                        data.setLengthOfTripTrackNumber(lengthOfTripNumber); //&&&&
                    }
                    data.pushTripTrackCode(ch);

                    if (--driverCodeCounter != 0) {
                        return TRIP_TICKET_CODE;
                    }

                    return TRY_FIND_DRIVER_CODE_IN_TRIP_TRACK;
                } else {
                    rememberInvalidPlaceInString(ch);
                    return INVALID_END;
                }
            }

            /**
             * Append current character to {@code temperature} field in {@code ParserData}
             * @param   ch   current character
             * @param   data   ParserData
             * @return  next State of StateMachine {@code State.TEMPERATURE} or {@code State.INVALID_END} if {@code ch} is not a number
             */
            private static State handleTemperature(char ch, ParserData data) {

                if (!DecoderUtil.isDigit(ch)) {
                    rememberInvalidPlaceInString(ch);
                    return INVALID_END;
                }
                data.pushTemperature(ch);
                return TEMPERATURE;
            }

            /**
             * Append current character to {@code temperature} field in {@code ParserData}
             * @param   ch   current character + or -
             * @param   data   ParserData
             * @return  next State of StateMachine {@code State.TEMPERATURE}
             */
            private static State handleTemperatureSign(char ch, ParserData data) {
                data.pushTemperature(ch);
                return TEMPERATURE;
            }

            /**
             * Append current character of cargo name to {@code name} field in {@code ParserData}
             * @param   ch   current character
             * @param   data   ParserData
             * @return  next State of StateMachine {@code State.VALID_END}
             * if {@code ch} is ' ' or {@code State.NAME}
             */
            private static State handleName(char ch, ParserData data) {

                if (ch == ' ') {
                    return VALID_END;
                }
                data.pushName(ch);
                return NAME;
            }

            /**
             * Remember {@code invalidChar} wrong char and it's index {@code indexOfError}
             * @param   ch   current character
             */
            private static void rememberInvalidPlaceInString(char ch) {
                invalidChar = ch;
                indexOfError = index;
            }
        }

        /**
         * Inner class of StateAutomate that represents parsed end decoded string
         * {@code danger} - indicates that cargo is danger
         * {@code frowy} - indicates that cargo is frowy
         * {@code driverCode} - contains driver code character
         * {@code driverCodeAgain} - contains driver code from  trip track code
         * {@code temperature} - contains temperature
         * {@code name} - contents octal number representation of cargo name
         * {@code lengthOfTripNumber} - indicates length od trip track code number.Depends on
         * {@code danger} and {@code frowy} fields
         * **/
        private class ParserData {
            private StringBuilder stringRow = new StringBuilder();
            private boolean danger = false;
            private boolean frowy = false;
            private StringBuilder driverCode = new StringBuilder();
            private StringBuilder driverCodeAgain = new StringBuilder();
            private StringBuilder tripTrackCode = new StringBuilder();
            private StringBuilder temperature = new StringBuilder();
            private StringBuilder name = new StringBuilder();
            private int lengthOfTripNumber = 0;

            public StringBuilder getResult() {

                if (!driverCodeAgain.toString().isEmpty()) {
                    tripTrackCode.append(driverCodeAgain);
                }
                StringBuilder cargoName = getStringCargoName();
                stringRow.append(",\"")
                        .append(driverCode).append("\",\"")
                        .append(tripTrackCode).append("\"")
                        .append(",\"").append(danger).append("\"")
                        .append(",\"").append(frowy).append("\"")
                        .append(temperature.toString().isEmpty() ? "," : ",\"" + DecoderUtil.formatTemperature(temperature) + "\"")
                        .append(",\"").append(cargoName).append("\"");

                return stringRow;
            }

            /**
             * Evaluates cargo name based on octal number representation.
             * If octal number representation contains symbol in significant
             * part of it - NumberFormatException e is caught.
             * For example, if {@code name} = "1631411561441", cargo name = "sand"
             * if {@code name} = "1431501451451631455A4h", cargo name = "wrong input for name".
             * Because octal numbers are: 143 150 145 145 163 145 5A4, non-significant
             * char - h. 5A4 cannot be parsed to decimal and NumberFormatException e will be thrown.
             *
             *
             * @return StringBuilder value of cargo name
             */
            private StringBuilder getStringCargoName() {
                StringBuilder cargoName;

                try {
                    cargoName = DecoderUtil.calculateCargoName(name);
                } catch (NumberFormatException e) {
                    cargoName = new StringBuilder("wrong input for name");
                }
                return cargoName;
            }

            public void pushDriverCode(char ch) {
                this.driverCode.append(ch);
            }

            public void pushTripTrackCode(char ch) {
                tripTrackCode.append(ch);
            }

            public void pushName(char ch) {
                name.append(ch);
            }

            public void pushTemperature(char ch) {
                temperature.append(ch);
            }


            public void addFirstElement(String wholeString) {
                stringRow.append("\"").append(wholeString).append("\"");
            }

            public boolean hasLengthOfTripTrackNumber() {
                return lengthOfTripNumber != 0;
            }

            public void setLengthOfTripTrackNumber(int lengthOfTripNumber) {
                driverCodeCounter = lengthOfTripNumber;
                this.lengthOfTripNumber = lengthOfTripNumber;
            }

            public void pushDriverCodeAgain(char ch) {
                driverCodeAgain.append(ch);
            }


            public void setDanger() {
                danger = true;
            }

            public void setFrowy() {
                frowy = true;
            }

            public boolean isDanger() {
                return danger;
            }

            public boolean isFrowy() {
                return frowy;
            }

            public String getDriverCode() {
                return driverCode.toString();
            }

        }
    }
}
