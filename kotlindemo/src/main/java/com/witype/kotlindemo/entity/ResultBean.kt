package com.witype.kotlindemo.entity

open class ResultBean {
    /**
     * fields : [{"id":"_id","type":"int4"},{"id":"quarter","type":"text"},{"id":"volume_of_mobile_data","type":"numeric"}]
     * total : 59
     * resource_id : a807b7ab-6cad-4aa6-87d0-e283a7353a0f
     * records : [{"quarter":"2004-Q3","volume_of_mobile_data":"0.000384","_id":1},{"quarter":"2004-Q4","volume_of_mobile_data":"0.000543","_id":2},{"quarter":"2005-Q1","volume_of_mobile_data":"0.00062","_id":3},{"quarter":"2005-Q2","volume_of_mobile_data":"0.000634","_id":4},{"quarter":"2005-Q3","volume_of_mobile_data":"0.000718","_id":5},{"quarter":"2005-Q4","volume_of_mobile_data":"0.000801","_id":6},{"quarter":"2006-Q1","volume_of_mobile_data":"0.00089","_id":7},{"quarter":"2006-Q2","volume_of_mobile_data":"0.001189","_id":8},{"quarter":"2006-Q3","volume_of_mobile_data":"0.001735","_id":9},{"quarter":"2006-Q4","volume_of_mobile_data":"0.003323","_id":10}]
     * limit : 10
     * _links : {"start":"/api/action/datastore_search?limit=10&resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f","next":"/api/action/datastore_search?offset=10&limit=10&resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f"}
     */
    var total = 0

    var resource_id: String? = null

    var limit = 0

    var records: List<RecordsBean>? = null

    override fun toString(): String {
        return "ResultBean{" +
                "total=" + total +
                ", resource_id='" + resource_id + '\'' +
                ", limit=" + limit +
                ", records=" + (if (records == null) 0 else records!!.size) +
                '}'
    }
}