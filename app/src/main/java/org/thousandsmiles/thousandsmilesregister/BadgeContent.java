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
 * Created by syd on 10/19/15.
 */
public class BadgeContent implements Parcelable {
    private String m_name;
    private String m_job;
    private String m_date;
    private Boolean m_dinnerPaid;
    private Boolean m_busPaid;
    private Boolean m_hotelPaid;

    public BadgeContent() {
    }

    void setName(String name) {
        m_name = name;
    }

    String getName() {
        return m_name;
    }

    void setJob(String job) {
        m_job = job;
    }

    String getJob() {
        return m_job;
    }

    void setDate(String date) {
        m_date = date;
    }

    String getDate() {
        return m_date;
    }

    void setDinnerPaid(Boolean dinnerPaid) {
        m_dinnerPaid = dinnerPaid;
    }

    Boolean getDinnerPaid() {
        return m_dinnerPaid;
    }

    void setHotelPaid(Boolean hotelPaid) {
        m_hotelPaid = hotelPaid;
    }

    Boolean getHotelPaid() {
        return m_hotelPaid;
    }

    void setBusPaid(Boolean busPaid) {
        m_busPaid = busPaid;
    }

    Boolean getBusPaid() {
        return m_busPaid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_name);
        dest.writeString(m_job);
        dest.writeString(m_date);
        dest.writeByte((byte) (m_busPaid ? 1 : 0));
        dest.writeByte((byte) (m_hotelPaid ? 1 : 0));
        dest.writeByte((byte) (m_dinnerPaid ? 1 : 0));
    }

    private BadgeContent(Parcel in) {
        m_name = in.readString();
        m_job = in.readString();
        m_date = in.readString();
        m_busPaid = in.readByte() != 0;
        m_hotelPaid = in.readByte() != 0;
        m_dinnerPaid = in.readByte() != 0;
    }

    public static final Parcelable.Creator<BadgeContent> CREATOR =
            new Parcelable.Creator<BadgeContent>() {
                public BadgeContent createFromParcel(Parcel in) {
                    return new BadgeContent(in);
                }

                public BadgeContent[] newArray(int size) {
                    return new BadgeContent[size];
                }
            };
}
