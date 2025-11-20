package com.uber;

import com.uber.database.ConnectionBDH2;
import org.h2.tools.Server;

import java.sql.Connection;

public class TestH2 {
    public static void main(String[] args) throws Exception {
        Server.createWebServer("-web", "-webAllowOthers", "-browser").start();
    }
}

