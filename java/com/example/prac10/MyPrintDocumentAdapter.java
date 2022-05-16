package com.example.prac10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
    Context context;
    PrintedPdfDocument mPdfDocument;
    int totalPages;

    public MyPrintDocumentAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         Bundle bundle) {
        mPdfDocument = new PrintedPdfDocument(context, newAttributes);

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        totalPages = computePageCount(newAttributes);

        if (totalPages > 0) {
            PrintDocumentInfo info = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalPages)
                    .build();

            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("Page count calculation failed.");
        }
    }

    private int computePageCount(PrintAttributes printAttributes) {
        int itemsPerPage = 4; //default item count for portrait mode

        PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();

        if(!pageSize.isPortrait()) {
            itemsPerPage = 6; // 6 items per page in landscape mode
        }

        int printItemCount = getPrintItemCount();

        return (int) Math.ceil(printItemCount / itemsPerPage);
    }

    private int getPrintItemCount() {
        return 0;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private boolean containsPage(PageRange[] pages, int i) {
        // check if contains
        return true;
    }

    @Override
    public void onWrite(PageRange[] pageRanges,
                        ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {
        // iterate over every page of the document.
        // check if it's in the output range
        int totalPages = 10;
        for (int i = 0; i < totalPages; i++) {
            // Check to see if this page is in the output range.
            if (containsPage(pageRanges, i)) {
                PdfDocument.Page[] writtenPagesArray;
                // If so, add it to writtenPagesArray. writtenPagesArray.size()
                // is used to compute the next output page index.
                //writtenPagesArray.append(writtenPagesArray.size(), i);
                PdfDocument.Page page = mPdfDocument.startPage(i);

                // check for cancellation
                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    mPdfDocument.close();
                    mPdfDocument = null;
                    return;
                }

                // Draw page content for printing
                drawPage(page);

                // Rendering is complete, so page can be finalized.
                mPdfDocument.finishPage(page);
            }
        }
    }

    private void drawPage(PdfDocument.Page page) {
        Canvas canvas = page.getCanvas();

        // units are in points (1/72 of an inch)
        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(36);
        canvas.drawText("Test Title", leftMargin, titleBaseLine, paint);

        paint.setTextSize(11);
        canvas.drawText("Test paragraph", leftMargin, titleBaseLine + 25, paint);

        paint.setColor(Color.BLUE);
        canvas.drawRect(100, 100, 172, 172, paint);
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }
}
