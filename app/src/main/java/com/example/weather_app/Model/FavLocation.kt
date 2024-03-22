package com.example.weather_app.Model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favLocation")
data class FavLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(), parcel.readDouble(), parcel.readString(), parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(address)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavLocation> {
        override fun createFromParcel(parcel: Parcel): FavLocation {
            return FavLocation(parcel)
        }

        override fun newArray(size: Int): Array<FavLocation?> {
            return arrayOfNulls(size)
        }
    }
}
