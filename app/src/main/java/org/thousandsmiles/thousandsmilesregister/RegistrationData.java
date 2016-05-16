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
public class RegistrationData {
    private static RegistrationData instance;
    private static boolean m_paidBus;
    private static boolean m_paidFee;
    private static boolean m_paidHotel;
    private static boolean m_paidDinner;

    public void setPaidDinner(boolean paid)
    {
       m_paidDinner = paid;
    }

    public void setPaidHotel(boolean paid)
    {
        m_paidHotel = paid;
    }

    public void setPaidFee(boolean paid)
    {
        m_paidFee = paid;
    }

    public void setPaidBus(boolean paid)
    {
        m_paidBus = paid;
    }

    public boolean getPaidDinner()
    {
        return m_paidDinner;
    }

    public boolean getPaidHotel()
    {
        return m_paidHotel;
    }

    public boolean getPaidBus()
    {
        return m_paidBus;
    }

    public boolean getPaidFee()
    {
        return m_paidFee;
    }

    public static RegistrationData getInstance() {
        if (instance == null) {
            instance = new RegistrationData();
        }
        return instance;
    }
}


