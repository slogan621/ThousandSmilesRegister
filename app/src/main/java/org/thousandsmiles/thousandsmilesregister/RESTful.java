/*
 * (C) Copyright Syd Logan 2016
 * (C) Copyright Thousand Smiles Foundation 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.thousandsmiles.thousandsmilesregister;

/**
 * Created by slogan on 10/3/15.
 */
public abstract class RESTful {
    private int m_status;

    // Auth string - example: "Token 123456789012345678901234567890abcdefabcd"
    private final String m_dbAPIToken = "Fix me";

    // URL for accessing DB RESTful API - example: https://foo.bar.org"
    private final String m_dbAPIHost = "Fix me";
    // Port for Database RESTful API
    private final String m_dbAPIPort = "443";

    public void setStatus(int status) {
        m_status = status;
    }
    protected String getDBAPIToken() { return m_dbAPIToken; }
    public int getStatus() {
        return m_status;
    }
    protected String getDBAPIHost() { return m_dbAPIHost; }
    protected String getDBAPIPort() { return m_dbAPIPort; }
}
