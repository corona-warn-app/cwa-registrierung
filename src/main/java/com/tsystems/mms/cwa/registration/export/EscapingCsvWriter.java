package com.tsystems.mms.cwa.registration.export;

import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.Writer;

public class EscapingCsvWriter extends CSVWriter {

    public EscapingCsvWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
        super(writer, separator, quotechar, escapechar, lineEnd);
    }

    @Override
    protected void writeNext(String[] nextLine, boolean applyQuotesToAll, Appendable appendable) throws IOException {
        for (int i = 0; i < nextLine.length; i++) {
            String value = nextLine[i];
            if (value == null) {
                continue;
            }
            value = value.trim();
            while (value.startsWith("=") ||
                    value.startsWith("-") ||
                    value.startsWith("+") ||
                    value.startsWith("@") ||
                    value.startsWith("\t") ||
                    value.startsWith("\r")) {
                value = value.substring(1);
            }
            nextLine[i] = value;
        }
        super.writeNext(nextLine, applyQuotesToAll, appendable);
    }
}
