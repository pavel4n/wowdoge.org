/**
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.dogecoin.core;

import com.google.dogecoin.params.MainNetParams;
import com.google.dogecoin.params.TestNet3Params;
import com.google.dogecoin.script.ScriptBuilder;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

import java.util.Arrays;

import static org.junit.Assert.*;

public class AddressTest {
    //static final NetworkParameters testParams = TestNet3Params.get();
    static final NetworkParameters mainParams = MainNetParams.get();

    @Test
    public void stringification() throws Exception {
        // Test a testnet address.
/*        Address a = new Address(testParams, Hex.decode("fda79a24e50ff70ff42f7d89585da5bd19d9e5cc"));
        assertEquals("n4eA2nbYqErp7H6jebchxAN59DmNpksexv", a.toString());
        assertFalse(a.isP2SHAddress());*/ //TODO: No Testnet info...

        Address b = new Address(mainParams, Hex.decode("908f4849864c54dadc24bf01c0c2dcd7180fd038"));
        assertEquals("DJKTVKGSDp5aUQzgtWyhbMtsXvW8s2vVRU", b.toString());
        //assertFalse(b.isP2SHAddress());
    }
    
    @Test
    public void decoding() throws Exception {
/*        Address a = new Address(testParams, "n4eA2nbYqErp7H6jebchxAN59DmNpksexv");
        assertEquals("fda79a24e50ff70ff42f7d89585da5bd19d9e5cc", Utils.bytesToHexString(a.getHash160()));*/ //TODO Testnet.

        Address b = new Address(mainParams, "DJKTVKGSDp5aUQzgtWyhbMtsXvW8s2vVRU");
        assertEquals("908f4849864c54dadc24bf01c0c2dcd7180fd038", Utils.bytesToHexString(b.getHash160()));
    }
    
    @Test
    public void errorPaths() {
        // Check what happens if we try and decode garbage.
        try {
            new Address(mainParams, "this is not a valid address!");
            fail();
        } catch (WrongNetworkException e) {
            fail();
        } catch (AddressFormatException e) {
            // Success.
        }

        // Check the empty case.
        try {
            new Address(mainParams, "");
            fail();
        } catch (WrongNetworkException e) {
            fail();
        } catch (AddressFormatException e) {
            // Success.
        }

/*        // Check the case of a mismatched network.
        try {
            new Address(mainParams, "17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL");
            fail();
        } catch (WrongNetworkException e) {
            // Success.
            assertEquals(e.verCode, MainNetParams.get().getAddressHeader());
            assertTrue(Arrays.equals(e.acceptableVersions, TestNet3Params.get().getAcceptableAddressCodes()));
        } catch (AddressFormatException e) {
            fail();
        }*/ //TODO testnet -.-
    }
    
    @Test
    public void getNetwork() throws Exception {
        NetworkParameters params = Address.getParametersFromAddress("DJKTVKGSDp5aUQzgtWyhbMtsXvW8s2vVRU");
        assertEquals(MainNetParams.get().getId(), params.getId());
/*        params = Address.getParametersFromAddress("n4eA2nbYqErp7H6jebchxAN59DmNpksexv");
        assertEquals(TestNet3Params.get().getId(), params.getId());*/
    }
    
/*    @Test //Doge doesn't have this.
    public void p2shAddress() throws Exception {
        // Test that we can construct P2SH addresses
        Address mainNetP2SHAddress = new Address(MainNetParams.get(), "35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU");
        assertEquals(mainNetP2SHAddress.version, MainNetParams.get().p2shHeader);
        assertTrue(mainNetP2SHAddress.isP2SHAddress());
        Address testNetP2SHAddress = new Address(TestNet3Params.get(), "2MuVSxtfivPKJe93EC1Tb9UhJtGhsoWEHCe");
        assertEquals(testNetP2SHAddress.version, TestNet3Params.get().p2shHeader);
        assertTrue(testNetP2SHAddress.isP2SHAddress());

        // Test that we can determine what network a P2SH address belongs to
        NetworkParameters mainNetParams = Address.getParametersFromAddress("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU");
        assertEquals(MainNetParams.get().getId(), mainNetParams.getId());
        NetworkParameters testNetParams = Address.getParametersFromAddress("2MuVSxtfivPKJe93EC1Tb9UhJtGhsoWEHCe");
        assertEquals(TestNet3Params.get().getId(), testNetParams.getId());

        // Test that we can convert them from hashes
        byte[] hex = Hex.decode("2ac4b0b501117cc8119c5797b519538d4942e90e");
        Address a = Address.fromP2SHHash(mainParams, hex);
        assertEquals("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU", a.toString());
        Address b = Address.fromP2SHHash(testParams, Hex.decode("18a0e827269b5211eb51a4af1b2fa69333efa722"));
        assertEquals("2MuVSxtfivPKJe93EC1Tb9UhJtGhsoWEHCe", b.toString());
        Address c = Address.fromP2SHScript(mainParams, ScriptBuilder.createP2SHOutputScript(hex));
        assertEquals("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU", c.toString());
    }*/
}
