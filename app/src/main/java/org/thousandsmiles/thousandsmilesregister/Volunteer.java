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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by syd on 6/24/15.
 */
public class Volunteer implements Parcelable {
    private String m_lastName = "";
    private String m_firstName = "";
    private Integer m_pin = 0;
    private Integer m_id = 0;
    private Integer m_registration = 0;
    private String m_job = "";

    public Volunteer(String firstName, String lastName, Integer pin, Integer id, Integer registration, String job) {
        m_lastName = lastName;
        m_firstName = firstName;
        m_pin = pin;
        m_id = id;
        m_registration = registration;
        m_job = job;
    }

    public String getLastName() {
       return m_lastName;
    }

    public Integer getId() { return m_id; }

    public Integer getRegistration() { return m_registration; }

    public Integer getPIN() { return m_pin; }


    public String getFirstName() {
        return m_firstName;
    }

    public String getJob() { return m_job; }

    public String repr() {
        return m_lastName + ", " + m_firstName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_lastName);
        dest.writeString(m_firstName);
        dest.writeInt(m_pin);
        dest.writeInt(m_id);
        dest.writeInt(m_registration);
        dest.writeString(m_job);
    }

    private Volunteer(Parcel in) {
        m_lastName = in.readString();
        m_firstName = in.readString();
        m_pin = in.readInt();
        m_id = in.readInt();
        m_registration = in.readInt();
        m_job = in.readString();
    }

    public static final Parcelable.Creator<Volunteer> CREATOR =
            new Parcelable.Creator<Volunteer>() {
                public Volunteer createFromParcel(Parcel in) {
                    return new Volunteer(in);
                }

                public Volunteer[] newArray(int size) {
                    return new Volunteer[size];
                }
            };
}
