package org.esa.beam.gpf.common.reproject.ui;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.gpf.ui.DefaultAppContext;
import org.esa.beam.framework.gpf.ui.SingleTargetProductDialog;
import org.esa.beam.framework.ui.AppContext;
import org.esa.beam.util.Debug;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.Map;

/**
 * User: Marco
 * Date: 16.08.2009
 */
class ReprojectionDialog extends SingleTargetProductDialog {

    private final ReprojectionForm form;

    public static void main(String[] args) {
        final DefaultAppContext context = new DefaultAppContext("Reproj");
        final ReprojectionDialog dialog = new ReprojectionDialog(true, "ReproTestDialog", null, context);
        dialog.show();

    }
    ReprojectionDialog(boolean orthorectify, final String title, final String helpID, AppContext appContext) {
        super(appContext, title, helpID);
        form = new ReprojectionForm(getTargetProductSelector(), orthorectify, appContext);
    }

    @Override
    protected boolean verifyUserInput() {
        if(form.getSourceProduct() == null) {
            showErrorDialog("No product to reproject selected.");
            return false;
        }

        try {
            final CoordinateReferenceSystem crs = form.getSelectedCrs();
            if(crs == null) {
                showErrorDialog("No 'Coordinate Reference System' selected.");
                return false;
            }
        } catch (FactoryException e) {
            showErrorDialog(String.format("Not able to create 'Coordinate Reference System':\n%s", e.getMessage()));
            Debug.trace(e);
            return false;
        }

        return true;
    }

    @Override
    protected Product createTargetProduct() throws Exception {
        final Map<String, Product> productMap = form.getProductMap();
        final Map<String, Object> parameterMap = form.getParameterMap();
        return GPF.createProduct("Reproject", parameterMap, productMap);
    }

    @Override
    public int show() {
        form.prepareShow();
        setContent(form);
        return super.show();
    }

    @Override
    public void hide() {
        form.prepareHide();
        super.hide();
    }

}
