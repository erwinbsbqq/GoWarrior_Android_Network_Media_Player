// ILocalServerService.aidl
package com.gowarrior.nmp.localserver;

// Declare any non-default types here with import statements

interface ILocalServerService {
    byte[] DoGet(String file);
    byte[] DoPost(String data);
}
