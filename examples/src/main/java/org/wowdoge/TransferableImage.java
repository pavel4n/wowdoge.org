package org.wowdoge;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class TransferableImage implements Transferable {

	  /* -- image for transfer */
	    
	  private Image image;

	  /* -- constructor */
	    
	  public TransferableImage( Image image ) {
	    this.image = image;
	  }

	  /* -- when is performed check for image type, returns image */
	    
	  @Override
	  public Object getTransferData( DataFlavor flavor ) throws UnsupportedFlavorException {
	    if ( isDataFlavorSupported( flavor ) ) {
	      return image;
	    } else {
	      throw new UnsupportedFlavorException( flavor );
	    }
	  }

	  /* -- method for checking supported type */
	    
	  @Override
	  public boolean isDataFlavorSupported( DataFlavor flavor ) {
	    return flavor == DataFlavor.imageFlavor;
	  }

	  /* -- get supported type */
	    
	  @Override
	  public DataFlavor[] getTransferDataFlavors() {
	    return new DataFlavor[]{ DataFlavor.imageFlavor };
	  }
	}