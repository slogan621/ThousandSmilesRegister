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

import java.util.ArrayList;

/**
 * Created by syd on 6/24/15.
 */
public class VolunteerMock {
    ArrayList<Volunteer> m_list;

    public VolunteerMock() {
        m_list = new ArrayList<Volunteer>();

        /*
        m_list.add(new Volunteer("Test1", "AAAA", 1234));
        m_list.add(new Volunteer("Test2", "AAAA", 1234));
        m_list.add(new Volunteer("Test3", "AAAA", 1234));
        m_list.add(new Volunteer("Test4", "AAAA", 1234));
        m_list.add(new Volunteer("Test5", "AAAA", 1234));
        m_list.add(new Volunteer("Test6", "AAAA", 1234));
        m_list.add(new Volunteer("Test7", "AAAA", 1234));
        m_list.add(new Volunteer("Test8", "AAAA", 1234));
        m_list.add(new Volunteer("Test9", "AAAA", 1234));
        m_list.add(new Volunteer("Test10", "AAAA", 1234));
        */
    }

    public ArrayList<Volunteer> getVolunteers(String letter) {

        ArrayList<Volunteer> ret = new ArrayList<Volunteer>();
        for (Volunteer x : m_list) {
            String lastName = x.getLastName();
            if (lastName.startsWith(letter)) {
                ret.add(x);
            }
        }
        return ret;
    }
}
