package org.feup.apm.acme.models

import android.os.Parcel
import android.os.Parcelable

data class ProductAmount(
    var uuid: String?,
    var amount: Int,
    var name:String?,
    var price: Float
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid)
        parcel.writeInt(amount)
        parcel.writeString(name)
        parcel.writeFloat(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductAmount

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid?.hashCode() ?: 0
    }

    companion object CREATOR : Parcelable.Creator<ProductAmount> {
        override fun createFromParcel(parcel: Parcel): ProductAmount {
            return ProductAmount(parcel)
        }

        override fun newArray(size: Int): Array<ProductAmount?> {
            return arrayOfNulls(size)
        }
    }

}
