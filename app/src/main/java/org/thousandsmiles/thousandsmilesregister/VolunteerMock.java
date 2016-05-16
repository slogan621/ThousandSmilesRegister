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
        m_list.add(new Volunteer("Bob", "Chalfa", 1234, 1, 1));
        m_list.add(new Volunteer("Sam", "Cooke", 1234, 1, 1));
        m_list.add(new Volunteer("Alan", "Cummings", 1234, 1, 1));
        /*
        m_list.add(new Volunteer("Victor", "Cruz", 1234));
        m_list.add(new Volunteer("Dale", "Chandler", 1234));
        m_list.add(new Volunteer("Sally", "Cox", 1234));
        m_list.add(new Volunteer("Elizabeth", "Childers", 1234));
        m_list.add(new Volunteer("John", "Chung", 1234));
        m_list.add(new Volunteer("Brendan", "Cagley", 1234));
        m_list.add(new Volunteer("Jean", "Calkin", 1234));
        m_list.add(new Volunteer("Kyle", "Campbell", 1234));
        m_list.add(new Volunteer("Gloria", "Chalfa", 1234));
        m_list.add(new Volunteer("Test1", "Chalfa", 1234));
        m_list.add(new Volunteer("Test2", "Chalfa", 1234));
        m_list.add(new Volunteer("Test3", "Chalfa", 1234));
        m_list.add(new Volunteer("Test4", "Chalfa", 1234));
        m_list.add(new Volunteer("Test5", "Chalfa", 1234));
        m_list.add(new Volunteer("Test6", "Chalfa", 1234));
        m_list.add(new Volunteer("Test7", "Chalfa", 1234));
        m_list.add(new Volunteer("Test8", "Chalfa", 1234));
        m_list.add(new Volunteer("Test9", "Chalfa", 1234));
        m_list.add(new Volunteer("Test10", "Chalfa", 1234));
        m_list.add(new Volunteer("Test11", "Chalfa", 1234));
        m_list.add(new Volunteer("Test12", "Chalfa", 1234));
        m_list.add(new Volunteer("Test13", "Chalfa", 1234));
        m_list.add(new Volunteer("Test14", "Chalfa", 1234));
        m_list.add(new Volunteer("Test15", "Chalfa", 1234));
        m_list.add(new Volunteer("Test16", "Chalfa", 1234));
        m_list.add(new Volunteer("Test17", "Chalfa", 1234));
        m_list.add(new Volunteer("Test18", "Chalfa", 1234));
        m_list.add(new Volunteer("Test19", "Chalfa", 1234));
        m_list.add(new Volunteer("Test20", "Chalfa", 1234));
        m_list.add(new Volunteer("Syd", "Logan", 1234));
        m_list.add(new Volunteer("Kim", "Muslusky", 1234));
        m_list.add(new Volunteer("Mark", "McAnelley", 1234));
        m_list.add(new Volunteer("Lydia", "Stewart", 1234));
        m_list.add(new Volunteer("Maria", "Lopez", 1234));
        m_list.add(new Volunteer("Terry", "Tanaka", 1234));
        m_list.add(new Volunteer("Dave", "Irwin", 1234));
        m_list.add(new Volunteer("Marc", "Lebovits", 1234));
        m_list.add(new Volunteer("Peter", "Pupping", 1234));
        m_list.add(new Volunteer("Mei", "Han", 1234));
        m_list.add(new Volunteer("Steve", "Leighty", 1234));
        m_list.add(new Volunteer("Teresa", "Misenhelter", 1234));
        m_list.add(new Volunteer("Beverly", "Jones", 1234));
        m_list.add(new Volunteer("Halle", "Plante", 1234));
        m_list.add(new Volunteer("Grace", "Logan", 1234));
        m_list.add(new Volunteer("Rod", "Logan", 1234));
        m_list.add(new Volunteer("Bobby", "Chalfa", 1234));
        m_list.add(new Volunteer("Briana", "Chalfa", 1234));
        m_list.add(new Volunteer("Moe", "Sepassi", 1234));
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
