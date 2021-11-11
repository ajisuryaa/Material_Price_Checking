package com.putrabatam.materialstore.utils;

public class Server_Configuration {
    //local_server
    public static final String address_server = "https://192.168.1.13/API_STORE_MATERIAL/view/";
    public static final String address_image = "http://192.168.1.13/API_STORE_MATERIAL/";

    //hosting_server
    //public static final String address_server = "https://qrstoreapps.000webhostapp.com/view/";
    //public static final String address_image = "https://qrstoreapps.000webhostapp.com/";

    public static final String address_login_admin = address_server + "login.php";
    public static final String address_add_new_material = address_server + "add_new_material.php";
    public static final String address_update_material = address_server + "update_material.php";
    public static final String address_delete_new_material = address_server + "delete_material.php";
    public static final String address_get_list_material = address_server + "show_list_material.php";
    public static final String address_scan_material = address_server + "scan_qr.php";
    public static final String address_get_list_price = address_server + "get_list_price_cart.php";
}
