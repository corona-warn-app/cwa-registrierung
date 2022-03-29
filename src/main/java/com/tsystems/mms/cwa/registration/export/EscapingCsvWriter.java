/*
 * Corona-Warn-App / cwa-map-registrierung
 *
 * (C) 2020, T-Systems International GmbH
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
