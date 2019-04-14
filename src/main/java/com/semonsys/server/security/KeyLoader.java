package com.semonsys.server.security;

import lombok.extern.java.Log;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;

@Log
class  KeyLoader {
    static PrivateKey load() {
        char[] password = "secret".toCharArray();
        String alias = "alias";
        PrivateKey pk = null;
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            log.warning(e.getMessage());
        }
        String configDir = System.getProperty("jboss.server.config.dir");
        String keystorePath = configDir + File.separator + "jwt.keystore";
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            assert ks != null;
            ks.load(fis, password);
            Key key = ks.getKey(alias, password);
            if (key instanceof PrivateKey) {
                pk = (PrivateKey) key;
            }
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
        return pk;
    }
}
