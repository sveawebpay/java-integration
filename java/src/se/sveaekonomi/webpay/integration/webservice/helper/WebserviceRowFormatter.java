package se.sveaekonomi.webpay.integration.webservice.helper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.RowBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.util.calculation.MathUtil;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;

public class WebserviceRowFormatter {

	private OrderBuilder<?> order;

	private double totalAmountExVat;
	private double totalAmountIncVat;
	
	private LinkedHashMap<Double,Double> totalAmountPerVatRateIncVat;
	private LinkedHashMap<Double,Double> totalAmountPerVatRateExVat;

	private ArrayList<SveaOrderRow> newRows;

	// flags for each order row specification method seen in the order
	private HashMap<String,Boolean> rowSpecificationTypesFound = new HashMap<String, Boolean>();

	private void checkRowSpecificationType( RowBuilder row, HashMap<String,Boolean> flags  ) {
		
        if (row.getAmountExVat() != null && row.getVatPercent() != null && row.getAmountIncVat() == null) {
        	flags.put("exvat_and_vatpercent_orderrow_found", Boolean.TRUE);
        }
        if (row.getAmountExVat() == null && row.getVatPercent() != null && row.getAmountIncVat() != null) {
        	flags.put("incvat_and_vatpercent_orderrow_found", Boolean.TRUE);
        }

        if (row.getAmountExVat() != null && row.getVatPercent() == null && row.getAmountIncVat() != null) {
        	flags.put("incvat_and_exvat_orderrow_found", Boolean.TRUE);
        }	
	}	
	
	public WebserviceRowFormatter(OrderBuilder<?> order) {
		this.order = order;
	}

	/**
	 * @return true iff no rows specified by ExVat and VatPercent in order
	 */
	private boolean checkUntaintedByExVatAndVatPercent() {
    	for (OrderRowBuilder row : order.getOrderRows()) {
    		checkRowSpecificationType( row, rowSpecificationTypesFound);
    	}
    	for (InvoiceFeeBuilder row : order.getInvoiceFeeRows()) {
    		checkRowSpecificationType( row, rowSpecificationTypesFound);
    	}    	
    	for (ShippingFeeBuilder row : order.getShippingFeeRows()) {
    		checkRowSpecificationType( row, rowSpecificationTypesFound);
    	}
    	
    	Boolean boolean1 = rowSpecificationTypesFound.get("exvat_and_vatpercent_orderrow_found");
		return (boolean1 == null); // i.e. we haven't seen exvat_and_vatpercent_orderrow_found
	} 	
	
	public ArrayList<SveaOrderRow> formatRows() {
		newRows = new ArrayList<SveaOrderRow>();		


		// calculate order row totals, used to calculate discounts split per vatrate
		calculateTotals();				

		// check if any rows were specified w/PriceIncludingVat = false, and if so, convert all rows to use legacy setting of flag = false
		// done first so as not to lose accuracy by converting back in/from serializeAmountAndVat later 
		boolean usePriceIncludingVat = checkUntaintedByExVatAndVatPercent();		
		
		formatOrderRows(usePriceIncludingVat);
		formatShippingFeeRows(usePriceIncludingVat);
		formatInvoiceFeeRows(usePriceIncludingVat);
		formatFixedDiscountRows(usePriceIncludingVat);
		formatRelativeDiscountRows(usePriceIncludingVat);
		
		return newRows;
	}

    private void increaseCumulativeVatRateAmounts( LinkedHashMap<Double, Double> amountPerVatRate, Double key, Double value ) {    	
        if (amountPerVatRate.containsKey(key)) {
        	amountPerVatRate.put(key,  value + amountPerVatRate.get(key) );
        }
        else {
        	amountPerVatRate.put(key, value);
        }	
    }		
	
	private void calculateTotals() {
		totalAmountIncVat = 0;
		totalAmountExVat = 0;
		
		totalAmountPerVatRateIncVat = new LinkedHashMap<Double, Double>();
		totalAmountPerVatRateExVat = new LinkedHashMap<Double, Double>();
		
		List<OrderRowBuilder> orderRows = order.getOrderRows();
		
		double vatPercent;
		double vatPercentAsHundredth;
		
		double amountExVat;
		double amountIncVat;
		double quantity;

		for (OrderRowBuilder existingRow : orderRows) {
			
			vatPercent = existingRow.getVatPercent() != null ? existingRow.getVatPercent() : 0;
			vatPercentAsHundredth = vatPercent * 0.01;
			
			amountExVat = existingRow.getAmountExVat() != null ? existingRow.getAmountExVat() : 0;
			amountIncVat = existingRow.getAmountIncVat() != null ? existingRow.getAmountIncVat() : 0;
			quantity = existingRow.getQuantity() != null ? existingRow.getQuantity() : 0;
			
            // amountExVat & vatPercent used to specify product price
			if (existingRow.getVatPercent() != null && existingRow.getAmountExVat() != null) {
				
                totalAmountExVat += amountExVat * quantity;
                totalAmountIncVat += (amountExVat + (vatPercentAsHundredth * amountExVat)) * quantity;

                increaseCumulativeVatRateAmounts(totalAmountPerVatRateIncVat, vatPercent, amountExVat*quantity*(1 + vatPercentAsHundredth) );                
                increaseCumulativeVatRateAmounts(totalAmountPerVatRateExVat, vatPercent, amountExVat*quantity );                
			}
            // amountIncVat & vatPercent used to specify product price
			else if (existingRow.getVatPercent() != null && existingRow.getAmountIncVat() != null) {
                totalAmountIncVat += amountIncVat * quantity;
                totalAmountExVat += (amountIncVat - ((vatPercentAsHundredth / (1 + vatPercentAsHundredth)) * amountIncVat)) * quantity;

                increaseCumulativeVatRateAmounts(totalAmountPerVatRateIncVat, vatPercent, amountIncVat*quantity );                
                increaseCumulativeVatRateAmounts(totalAmountPerVatRateExVat, vatPercent, amountExVat*quantity );                
			}
            // no vatPercent given
			else {
                totalAmountIncVat += amountIncVat * quantity;
                totalAmountExVat += amountExVat * quantity;

                double vatRate = (amountIncVat == 0.0 || amountExVat == 0.0) ? 0 : 
                    ((amountIncVat / amountExVat) - 1) * 100;
                double vatRateAsHundredth = vatPercent * 0.01;
                
                increaseCumulativeVatRateAmounts(totalAmountPerVatRateIncVat, vatRate, (amountExVat * quantity * (1 + vatRateAsHundredth)) );                
                increaseCumulativeVatRateAmounts(totalAmountPerVatRateExVat, vatRate, amountExVat*quantity );                                
			}
		}
	}
	
	private <T extends RowBuilder> void formatRowLists(List<T> rows, boolean usePriceIncludingVat) {
		for (RowBuilder existingRow : rows) {

			// if fixedDiscount row, create one or more discount rows, if needed calculate vat split across existing order row vat rates
			if (FixedDiscountBuilder.class.equals(existingRow.getClass())) {
				
				// incvat set only, calculate discount from amount inc vat
				if (existingRow.getAmountIncVat() != null && existingRow.getVatPercent() == null && existingRow.getAmountExVat() == null) 
				{
				
                	for (double vatRate : totalAmountPerVatRateIncVat.keySet()) {
                    	SveaOrderRow orderRow = newRowBasedOnExisting(existingRow);

                        double amountAtThisVatRateIncVat = totalAmountPerVatRateIncVat.get(vatRate);

                        if (totalAmountPerVatRateIncVat.size() > 1) {
                            String name = existingRow.getName();
                            String description = existingRow.getDescription();

                            orderRow.Description = formatDiscountRowDescription(name, description, (long) vatRate);
                        }

                        double discountAtThisVatRateIncVat = existingRow.getAmountIncVat() * (amountAtThisVatRateIncVat / totalAmountIncVat);
                        double discountAtThisVatRateExVat = discountAtThisVatRateIncVat - discountAtThisVatRateIncVat * MathUtil.reverseVatRate(vatRate);

                        if( usePriceIncludingVat ) {
                        	orderRow.PricePerUnit = -MathUtil.bankersRound( convertExVatToIncVat(discountAtThisVatRateExVat, vatRate) );
                        	orderRow.VatPercent = vatRate;
                        	orderRow.PriceIncludingVat = true;                        	
                        }
                        else {
                        	orderRow.PricePerUnit = -MathUtil.bankersRound(discountAtThisVatRateExVat);
                        	orderRow.VatPercent = vatRate;
                        	orderRow.PriceIncludingVat = false;
                        }
                        newRows.add(orderRow);
                    }
                }

				// exvat set only, calculate discount from amount ex vat
				if (existingRow.getAmountIncVat() == null && existingRow.getVatPercent() == null && existingRow.getAmountExVat() != null) 
				{

                	for (double vatRate : totalAmountPerVatRateExVat.keySet()) {
                    	SveaOrderRow orderRow = newRowBasedOnExisting(existingRow);

                        double amountAtThisVatRateExVat = totalAmountPerVatRateExVat.get(vatRate);

                        if (totalAmountPerVatRateExVat.size() > 1) {
                            String name = existingRow.getName();
                            String description = existingRow.getDescription();

                            orderRow.Description = formatDiscountRowDescription(name, description, (long) vatRate);
                        }

                        double discountAtThisVatRateExVat = existingRow.getAmountExVat() * (amountAtThisVatRateExVat / totalAmountExVat) ;                     

                        if( usePriceIncludingVat ) {
                        	orderRow.PricePerUnit = -MathUtil.bankersRound( convertExVatToIncVat(discountAtThisVatRateExVat, vatRate) );	// TODO 
                        	orderRow.VatPercent = vatRate;
                        	orderRow.PriceIncludingVat = true;                        	
                        }
                        else {
                        	orderRow.PricePerUnit = -MathUtil.bankersRound(discountAtThisVatRateExVat);
                        	orderRow.VatPercent = vatRate;
                        	orderRow.PriceIncludingVat = false;
                        }
                        newRows.add(orderRow);
                    }
                }				
				
				// incvat, vatpercent set
                else if (existingRow.getAmountIncVat() != null && existingRow.getVatPercent() != null && existingRow.getAmountExVat() == null)
                {
                	SveaOrderRow orderRow = newRowBasedOnExisting(existingRow);

                	double vatRate = existingRow.getVatPercent();
                	double discountAtThisVatRateIncVat = existingRow.getAmountIncVat();
                	double discountAtThisVatRateExVat = discountAtThisVatRateIncVat - discountAtThisVatRateIncVat * MathUtil.reverseVatRate(vatRate);
                    
                    if( usePriceIncludingVat ) {
                    	orderRow.PricePerUnit = -MathUtil.bankersRound( convertExVatToIncVat(discountAtThisVatRateExVat, vatRate) );	// TODO 
                    	orderRow.VatPercent = vatRate;
                    	orderRow.PriceIncludingVat = true;                        	
                    }
                    else {
                    	orderRow.PricePerUnit = -MathUtil.bankersRound(discountAtThisVatRateExVat);
                    	orderRow.VatPercent = vatRate;
                    	orderRow.PriceIncludingVat = false;
                    }
                                     
                    newRows.add(orderRow);
                }
				
				// exvat, vatpercent set
                else if (existingRow.getAmountIncVat() == null && existingRow.getVatPercent() != null && existingRow.getAmountExVat() != null)
                {
                	SveaOrderRow orderRow = newRowBasedOnExisting(existingRow);

                    if( usePriceIncludingVat ) {
                    	orderRow.PricePerUnit = -MathUtil.bankersRound( convertExVatToIncVat(existingRow.getAmountExVat(), existingRow.getVatPercent()) );	// TODO 
                    	orderRow.VatPercent = existingRow.getVatPercent();
                    	orderRow.PriceIncludingVat = true;                        	
                    }
                    else {
                        orderRow.PricePerUnit = -MathUtil.bankersRound(existingRow.getAmountExVat());
                        orderRow.VatPercent = existingRow.getVatPercent();
                    	orderRow.PriceIncludingVat = false;
                    }

                    newRows.add(orderRow);
                }
			}
		
			// if relativeDiscount row, calculate vat, split over several rows if needed.
			else if (RelativeDiscountBuilder.class.equals(existingRow.getClass())) {
				for (double vatRate : totalAmountPerVatRateIncVat.keySet()) {
                	SveaOrderRow orderRow = newRowBasedOnExisting(existingRow);

                    double amountAtThisVatRateIncVat = totalAmountPerVatRateIncVat.get(vatRate);

                    if (totalAmountPerVatRateIncVat.size() > 1)
                    {
                        String name = existingRow.getName();
                        String description = existingRow.getDescription();

                        orderRow.Description = formatDiscountRowDescription(name, description, (long) vatRate);
                    }

                    double amountAtThisVatRateExVat = amountAtThisVatRateIncVat - amountAtThisVatRateIncVat * MathUtil.reverseVatRate(vatRate);
                    double discountExVat = amountAtThisVatRateExVat * (existingRow.getDiscountPercent() / 100);
                    
                    if( usePriceIncludingVat ) {
                    	orderRow.PricePerUnit = -MathUtil.bankersRound( convertExVatToIncVat(discountExVat, vatRate) );
                    	orderRow.VatPercent = vatRate;
                    	orderRow.PriceIncludingVat = true;                        	
                    }
                    else {
                        orderRow.PricePerUnit = -MathUtil.bankersRound(discountExVat);
                        orderRow.VatPercent = vatRate;
                    	orderRow.PriceIncludingVat = false;
                    }

                    //Relative discounts is a special case where we want to use the discount percent in calculations
                    //but not display it on the order row. Since that would imply that it's a discount on a discount.
                    //So that is why we force the value to 0 below.
                    orderRow.DiscountPercent = 0;

                    newRows.add(orderRow);
                }
			}
			
			// other row types (order row, shipping fee, invoice fee)
			else {
            	// serializeAmountAndVat gives preference to ExVat and vatPercent, i.e. PriceIncludingVat flag is set to false (legacy), so we need to
				// make sure ExVat and VatPercent are set/not set according to whether we want to force order to use price excluding vat or not in request
                if( usePriceIncludingVat == true  ) {                	
                	//vat satt ? använd vat : räkna om från incvat, exvat
                	//incvat satt ? använd incvat : räkna om från vat, exvat 
                	//exvat = null
                	double vat;
                	if( existingRow.getVatPercent() != null ) { 
            			vat = existingRow.getVatPercent();
                	} 
                	else {
                		vat = ((existingRow.getAmountIncVat()/existingRow.getAmountExVat())-1)*100;
                	}
                	
    				double incvat;
                	if( existingRow.getAmountIncVat() != null ) {
                		incvat = existingRow.getAmountIncVat();                	
	                }
	                else {
            			incvat = (existingRow.getAmountExVat()*((vat)+1));
	                }
                	
                	newRows.add(serializeAmountAndVat(null, vat,
                			incvat, newRowBasedOnExisting(existingRow)));
                }
                if( (usePriceIncludingVat == false ) ) {
                	//vat satt ? använd vat : räkna om från incvat, exvat
                	//exvat satt ? använd exvat : räkna om från vat, incvat 
                	//incvat = null
                	double vat;
                	if( existingRow.getVatPercent() != null ) { 
            			vat = existingRow.getVatPercent();
                	} 
                	else {
                		vat = ((existingRow.getAmountIncVat()/existingRow.getAmountExVat())-1)*100;
                	}
    				double exvat;
                	if( existingRow.getAmountExVat() != null ) {
                		exvat = MathUtil.bankersRound( existingRow.getAmountExVat() );                	
	                }
	                else {
            			exvat = MathUtil.bankersRound( (existingRow.getAmountIncVat()/((vat/100)+1)) );
	                }                	
                
                	newRows.add(serializeAmountAndVat(exvat, vat,
                			null, newRowBasedOnExisting(existingRow)));                  	
                }
			}
		}
	}
	
    private double convertExVatToIncVat(double amountExVat, double vatPercent) {
		return amountExVat * (1+vatPercent/100);
	}

	private SveaOrderRow newRowBasedOnExisting(RowBuilder existingRow) {
        SveaOrderRow newOrderRow = new SveaOrderRow();
        newOrderRow = serializeOrder(existingRow.getArticleNumber(), existingRow.getDescription(),
                                     existingRow.getName(), existingRow.getUnit(), newOrderRow);

        newOrderRow.DiscountPercent = existingRow.getDiscountPercent();
        newOrderRow.NumberOfUnits = existingRow.getQuantity();

        return newOrderRow;
    }
    
    private String formatDiscountRowDescription(String name, String description, long vatRate) {
        String formattedDescription;
        if (name != null) {
            formattedDescription = name + (description == null ? "" : ": " + description);
        }
        else {
            formattedDescription = description != null ? description : "";
        }

        formattedDescription += " (" + vatRate + "%)";

        return formattedDescription;
    }

	private void formatOrderRows(boolean usePriceIncludingVat) {
		formatRowLists(order.getOrderRows(), usePriceIncludingVat);
	}

	private void formatShippingFeeRows(boolean usePriceIncludingVat) {
		if (this.order.getShippingFeeRows() == null) {
			return;
		}
		formatRowLists(order.getShippingFeeRows(), usePriceIncludingVat);
	}

	private void formatInvoiceFeeRows(boolean usePriceIncludingVat) {
		if (this.order.getInvoiceFeeRows() == null) {
			return;
		}
		formatRowLists(order.getInvoiceFeeRows(), usePriceIncludingVat);
	}

	private void formatFixedDiscountRows(boolean usePriceIncludingVat) {
		if (this.order.getFixedDiscountRows() == null) {
			return;
		}
		formatRowLists(order.getFixedDiscountRows(), usePriceIncludingVat);
	}

	private void formatRelativeDiscountRows(boolean usePriceIncludingVat) {
		if (this.order.getRelativeDiscountRows() == null) {
			return;
		}
		formatRowLists(order.getRelativeDiscountRows(), usePriceIncludingVat);
	}

	// populate SveaOrderRow from passed arguments (taken from OrderRow)
	private SveaOrderRow serializeOrder(String articleNumber, String description, String name, String unit, SveaOrderRow orderRow) {
		if (articleNumber != null) {
			orderRow.ArticleNumber = articleNumber;
		}

		if (name != null) {
			orderRow.Description = name + (description == null ? "" : ": " + description);
		}
		else {
			orderRow.Description = description == null ? "" : description;
		}

		if (unit != null) {
			orderRow.Unit = unit;
		}	

		return orderRow;
	}

	// serializeAmountAndVat gives preference to ExVat and vatPercent, i.e. PriceIncludingVat flag is set to false (legacy)
	private SveaOrderRow serializeAmountAndVat(Double amountExVat, Double vatPercent, Double amountIncVat, SveaOrderRow orderRow) {
		if (vatPercent != null && amountExVat != null) {
			orderRow.PricePerUnit = amountExVat;
			orderRow.VatPercent = vatPercent;
			orderRow.PriceIncludingVat = false;
		}
		else if (vatPercent != null && amountIncVat != null) {
			orderRow.PricePerUnit = amountIncVat;
			orderRow.VatPercent = vatPercent;
			orderRow.PriceIncludingVat = true;
		}
		else if (amountExVat != null && amountIncVat != null) {
			orderRow.PricePerUnit = amountIncVat;
			orderRow.VatPercent = ((amountIncVat / amountExVat) - 1) * 100;
			orderRow.PriceIncludingVat = true;
		}

		return orderRow;
	}
}
