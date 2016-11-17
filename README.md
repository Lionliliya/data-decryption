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

Results
----------

Input line                | Decoded line in CSV
--------------------------|------------------------------
RMuiRdf010160141151156164 | "RMuiRdf010160141151156164","RMui","Rdf010","true","true",,"paint"
lims8r3860lims1631411561441| "lims8r3860lims1631411561441","lims","r3860lims","false","false",,"sand"
GZQRyr6870GZQR+0041431501451451631455A | "GZQRyr6870GZQR+0041431501451451631455A","GZQR","r6870GZQR","false","false","+4","cheese"
RMuiRdfd010160141151156164 | Wrong input format at index 7 char = "d"
