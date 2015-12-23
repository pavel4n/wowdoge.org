/**
 * Copyright 2014 wowdoge.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wowdoge;

import java.io.*;
import java.util.prefs.*;

public class PrefObj
{
  // Max byte count is 3/4 max string length (see Preferences
  // documentation).
  static private final int pieceLength =
    ((3*Preferences.MAX_VALUE_LENGTH)/4);

  static private byte[] object2Bytes( Object o ) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream( baos );
    oos.writeObject( o );
    return baos.toByteArray();
  }

  static private byte[][] breakIntoPieces( byte raw[] ) {
    int numPieces = (raw.length + pieceLength - 1) / pieceLength;
    byte pieces[][] = new byte[numPieces][];
    for (int i=0; i<numPieces; ++i) {
      int startByte = i * pieceLength;
      int endByte = startByte + pieceLength;
      if (endByte > raw.length) endByte = raw.length;
      int length = endByte - startByte;
      pieces[i] = new byte[length];
      System.arraycopy( raw, startByte, pieces[i], 0, length );
    }
    return pieces;
  }

  static private void writePieces( Preferences prefs, String key,
      byte pieces[][] ) throws BackingStoreException {
    Preferences node = prefs.node( key );
    node.clear();
    for (int i=0; i<pieces.length; ++i) {
      node.putByteArray( ""+i, pieces[i] );
    }
  }

  static private byte[][] readPieces( Preferences prefs, String key )
      throws BackingStoreException {
    Preferences node = prefs.node( key );
    String keys[] = node.keys();
    int numPieces = keys.length;
    byte pieces[][] = new byte[numPieces][];
    for (int i=0; i<numPieces; ++i) {
      pieces[i] = node.getByteArray( ""+i, null );
    }
    return pieces;
  }

  static private byte[] combinePieces( byte pieces[][] ) {
    int length = 0;
    for (int i=0; i<pieces.length; ++i) {
      length += pieces[i].length;
    }
    byte raw[] = new byte[length];
    int cursor = 0;
    for (int i=0; i<pieces.length; ++i) {
      System.arraycopy( pieces[i], 0, raw, cursor, pieces[i].length );
      cursor += pieces[i].length;
    }
    return raw;
  }

  static private Object bytes2Object( byte raw[] )
      throws IOException, ClassNotFoundException {
    ByteArrayInputStream bais = new ByteArrayInputStream( raw );
    ObjectInputStream ois = new ObjectInputStream( bais );
    Object o = ois.readObject();
    return o;
  }

  static public void putObject( Preferences prefs, String key, Object o )
      throws IOException, BackingStoreException, ClassNotFoundException {
    byte raw[] = object2Bytes( o );
    byte pieces[][] = breakIntoPieces( raw );
    writePieces( prefs, key, pieces );
  }

  static public Object getObject( Preferences prefs, String key )
      throws IOException, BackingStoreException, ClassNotFoundException {
    byte pieces[][] = readPieces( prefs, key );
    byte raw[] = combinePieces( pieces );
    Object o = bytes2Object( raw );
    return o;
  }
}
