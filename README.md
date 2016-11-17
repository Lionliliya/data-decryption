# data-decryption
Decryption of the list of incoming lines, and save the resulting code numbers data in CSV-file in a table

DESCRIPTION
------------
On application input is a string list cipher specified in the specific format 
and containing information on scheduled freight. The program should generate a list 
of inbound decryption cipher strings and save the resulting data to a CSV file as a 
table, using specific column names, types of output values and decryption algorithms.


EXAMPLE
-----------
`Source Code:` CAZgRf820167151156145

`CSV:`

"Cipher", "driver code" "code track paper"," dangerous "," fragile "," Temperature "," name "

"CAZgRf820167151156145", "CAZg", "Rf820", "false", "true" ,, "wine"

Classes
-----------
Class           | Description
----------------|----------------------
Decoder.class   | Contains method for performing parsing operation of encoded string to csv format. Methods in Decoder are static, so you can use it without initializing the instance of Decoder class. After returning result of parsing all static fields of Decoder are resent to initial value.
DecoderUtil.class| Contains methods for performing basic validation and transforming operation for {@code DecoderUtil.class}
FileUtil.class        | Provide methods for performing reading from and writing to file {@code inputFilePath} - path to file from where encoded data are reading {@code csvFilePath} - path to file where decoded data are writing
