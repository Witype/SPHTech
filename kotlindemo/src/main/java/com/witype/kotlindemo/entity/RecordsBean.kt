package com.witype.kotlindemo.entity

import java.util.regex.Pattern

class RecordsBean : CompareAble {
    /**
     * quarter : 2004-Q3
     * volume_of_mobile_data : 0.000384
     * _id : 1
     */
    var quarterQuaterNum = 0
        get() {
            try {
                if (quarter != null && !quarter!!.isEmpty()) {
                    val pattern = Pattern.compile("Q[0-4]{1}")
                    val matcher = pattern.matcher(quarter)
                    val result = if (matcher.find()) matcher.group(0).replace("Q", "0") else "0"
                    field = Integer.parseInt(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return field
        }
        private set
    var quarterYearNum = 0
        get() {
            try {
                if (quarter != null && !quarter!!.isEmpty()) {
                    val pattern = Pattern.compile("[0-9]{3,4}")
                    val matcher = pattern.matcher(quarter)
                    val result = if (matcher.find()) matcher.group(0) else "0"
                    field = Integer.parseInt(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return field
        }
        private set

    var quarter: String? = null

    var volume_of_mobile_data = 0.0

    private var volume = 0.0

    private var _id = 0

    var volume_offset = 0.0

    var total_of_year = 0.0
        get() = if (field <= 0) volume_of_mobile_data else field

    fun getVolume(): String {
        return if (volume <= 0) volume.toString() else volume.toString()
    }

    fun setVolume(volume: Double) {
        this.volume = volume
    }


    fun get_id(): Int {
        return _id
    }

    fun set_id(_id: Int) {
        this._id = _id
    }

    override val compareKey: Int
        get() = String.format("%s%s", quarterYearNum, quarterQuaterNum).toInt()

    override fun toString(): String {
        return "RecordsBean{" +
                "quarter='" + quarter + '\'' +
                ", volume_of_mobile_data=" + volume_of_mobile_data +
                ", _id=" + get_id() +
                ", volume_offset=" + volume_offset +
                ", total_of_year=" + total_of_year +
                '}'
    }
}