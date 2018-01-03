/*
 * Copyright Sergius Dell DevLab
 */

/**
 *
 * @author emil
 */
/*
 * Credits: SonarSource, dev@sonar.codehaus.org
 */

import java.io.IOException;
import java.io.Reader;
import java.nio.IntBuffer;
import java.nio.charset.Charset;

public class TypeConverter {

  private static final int INITIAL_BUFFER_SIZE = 3200;
  private static final int LF = '\n';
  private static final int NEL = 0x15;
  private static final int WS = ' ';
  static final Charset CP1047 = Charset.forName("Cp1047");
  private static final char[] NON_PRINTABLE_EBCDIC_CHARS = new char[] { 0x00, 0x01, 0x02, 0x03, 0x9C, 0x09, 0x86, 0x7F, 0x97, 0x8D, 0x8E,
    0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x9D, 0x85, 0x08, 0x87, 0x18, 0x19, 0x92, 0x8F, 0x1C, 0x1D, 0x1E, 0x1F, 0x80,
    0x81, 0x82, 0x83, 0x84, 0x0A, 0x17, 0x1B, 0x88, 0x89, 0x8A, 0x8B, 0x8C, 0x05, 0x06, 0x07, 0x90, 0x91, 0x16, 0x93, 0x94, 0x95, 0x96,
    0x04, 0x98, 0x99, 0x9A, 0x9B, 0x14, 0x15, 0x9E, 0x1A, 0x20, 0xA0 };

  private final Charset ebcdicCharset;
  private final Charset outputCharset;
  private int fixedLength = -1;
  String convertedOutput;
  int[] ebcdicInput;
  
  public TypeConverter(Charset ebcdicCharset, Charset outputCharset, int[] ebcdicInput) {
        this.ebcdicInput = ebcdicInput;
        this.convertedOutput = null;
        this.ebcdicCharset = ebcdicCharset;
        this.outputCharset = outputCharset;
  }

    TypeConverter(Charset CP1047, Charset US_ASCII, IntBuffer reelBuffer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  public void setFixedLength(int numberOfColumn) {
        this.fixedLength = numberOfColumn;
  }

    String getConvertedOutput() {return this.convertedOutput;};

  private void convert() throws IOException {
    int convertedChar;
    for (int index = 0; index < ebcdicInput.length; index++) {
      int character = ebcdicInput[index];
      if (fixedLength != -1 && index > 0 && index % fixedLength == 0) {
        convertedOutput+= LF;
      }
      if (fixedLength == -1 && character == NEL) {
        convertedChar = LF;
      } else {
        convertedChar = replaceNonPrintableCharacterByWhitespace(character);
      }
      convertedOutput += convertedChar;
    }
  }

  private int replaceNonPrintableCharacterByWhitespace(int character) {
    for (char nonPrintableChar : NON_PRINTABLE_EBCDIC_CHARS) {
      if (nonPrintableChar == (char) character) {
        return WS;
      }
    }
    return character;
  }

  private int[] loadContent(Reader reader) throws IOException {
    int[] buffer = new int[INITIAL_BUFFER_SIZE];
    int bufferIndex = 0;
    int bufferSize = buffer.length;
    int character;
    while ((character = reader.read()) != -1) {
      if (bufferIndex == bufferSize) {
        buffer = resizeArray(buffer, bufferSize + INITIAL_BUFFER_SIZE);
        bufferSize = buffer.length;
      }
      buffer[bufferIndex++] = character;
    }
    return resizeArray(buffer, bufferIndex);
  }

  final int[] resizeArray(int[] orignalArray, int newSize) {
    int[] resizedArray = new int[newSize];
    for (int i = 0; i < newSize && i < orignalArray.length; i++) {
      resizedArray[i] = orignalArray[i];
    }
    return resizedArray;
  }

}

class EbcdicToAsciiConverterException extends RuntimeException {

  public EbcdicToAsciiConverterException(String message, Throwable e) {
    super(message, e);
  }

  public EbcdicToAsciiConverterException(String message) {
    super(message);
  }

}