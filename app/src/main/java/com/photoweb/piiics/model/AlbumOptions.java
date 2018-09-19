package com.photoweb.piiics.model;

import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.model.PriceReferences.APIReference;
import com.photoweb.piiics.model.PriceReferences.BookReference;
import com.photoweb.piiics.utils.PriceReferences;

/**
 * Created by thomas on 27/09/2017.
 */

public class AlbumOptions {

    BookReference noLogoOption;
    BookReference strongCoverOption;
    BookReference varnishedPagesOption;
    BookReference mateCoverOption;
    BookReference additionalBook;

    boolean hasNoLogo;
    boolean hasStrongCover;
    boolean hasVarnishedPages;
    boolean hasMateCover;
    int bookQuantity;


    public AlbumOptions() {
        this.noLogoOption = PriceReferences.findBookReferenceByName("logo") ;
        if(this.noLogoOption == null) this.noLogoOption = new BookReference(5, "logo", "1.99", "1.99");

        this.strongCoverOption = PriceReferences.findBookReferenceByName("prestige");
        if(this.strongCoverOption == null) this.strongCoverOption = new BookReference(8, "prestige", "9.99", "14.99");

        this.varnishedPagesOption = PriceReferences.findBookReferenceByName("varnished");
        if(this.varnishedPagesOption == null) this.varnishedPagesOption = new BookReference(9, "varnished", "3.99", "3.99");

        this.mateCoverOption = PriceReferences.findBookReferenceByName("notshiny");
        if(this.mateCoverOption == null) this.mateCoverOption = new BookReference(7, "notshiny", "2.99", "2.99");

        this.additionalBook = PriceReferences.findBookReferenceByName("book");
        if(this.additionalBook == null) this.additionalBook = new BookReference(6, "book", "9.99", "9.99");

        this.hasNoLogo = false;
        this.hasStrongCover = false;
        this.hasVarnishedPages = false;
        this.hasMateCover = false;
        this.bookQuantity = 1;
    }

    public int getNoLogoOptionPrice() throws PriceSecurityException {
        if (hasNoLogo) {
            return APIReference.getPriceInCts(noLogoOption.getCurPriceStr());
        } else {
            return 0;
        }
    }

    public int getStrongCoverOptionPrice() throws PriceSecurityException {
        if (hasStrongCover) {
            return APIReference.getPriceInCts(strongCoverOption.getCurPriceStr());
        } else {
            return 0;
        }
    }

    public int getVarnishedPagesOptionPrice() throws PriceSecurityException {
        if (hasVarnishedPages) {
            return APIReference.getPriceInCts(varnishedPagesOption.getCurPriceStr());
        } else {
            return 0;
        }
    }

    public int getMateCoverOptionPrice() throws PriceSecurityException {
        if (hasMateCover) {
            return APIReference.getPriceInCts(mateCoverOption.getCurPriceStr());
        } else {
            return 0;
        }
    }

    public int getAdditionalBooksPrice() throws PriceSecurityException {
        if (bookQuantity > 1) {
            return APIReference.getPriceInCts(additionalBook.getCurPriceStr());
          //  return bookPrice * (bookQuantity - 1);
        } else {
            return 0;
        }
    }

    public int getOptionsTotalPrice()  throws PriceSecurityException{
        int totalPrice = 0;
        totalPrice += getNoLogoOptionPrice() * bookQuantity;
        totalPrice += getStrongCoverOptionPrice() * bookQuantity;
        totalPrice += getVarnishedPagesOptionPrice() * bookQuantity;
        totalPrice += getMateCoverOptionPrice() * bookQuantity;
        totalPrice += getAdditionalBooksPrice() * (bookQuantity - 1);
        return totalPrice;
    }

    //getters and setters---------------------------------------------------------------------------

    public boolean isHasNoLogo() {
        return hasNoLogo;
    }

    public void setHasNoLogo(boolean hasNoLogo) {
        this.hasNoLogo = hasNoLogo;
    }

    public boolean isHasStrongCover() {
        return hasStrongCover;
    }

    public void setHasStrongCover(boolean hasStrongCover) {
        this.hasStrongCover = hasStrongCover;
    }

    public boolean isHasVarnishedPages() {
        return hasVarnishedPages;
    }

    public void setHasVarnishedPages(boolean hasVarnishedPages) {
        this.hasVarnishedPages = hasVarnishedPages;
    }

    public boolean isHasMateCover() {
        return hasMateCover;
    }

    public void setHasMateCover(boolean hasMateCover) {
        this.hasMateCover = hasMateCover;
    }

    public int getBookQuantity() {
        return bookQuantity;
    }

    public void setBookQuantity(int bookQuantity) {
        this.bookQuantity = bookQuantity;
    }

    public BookReference getNoLogoOption() {
        return noLogoOption;
    }

    public BookReference getStrongCoverOption() {
        return strongCoverOption;
    }

    public BookReference getVarnishedPagesOption() {
        return varnishedPagesOption;
    }

    public BookReference getMateCoverOption() {
        return mateCoverOption;
    }

    public BookReference getAdditionalBook() {
        return additionalBook;
    }

    //----------------------------------------------------------------------------------------------
}
