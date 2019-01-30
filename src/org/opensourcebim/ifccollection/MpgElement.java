package org.opensourcebim.ifccollection;

import org.opensourcebim.nmd.NmdProductCard;

/**
 * Storage container to map mpgOject to the Nmdproducts
 * @author Jasper Vijverberg
 *
 */
public class MpgElement {
	// id's
	private String BimBotIdentifier;
	
	private String ifcName;
	private NmdProductCard nmdProductCard;
	private MpgObject mpgObject;
	
	public MpgElement(String name)
	{
		ifcName = name;
	}
	
	public void setMpgObject(MpgObject mpgObject) {
		this.mpgObject = mpgObject;
	}
	
	public MpgObject getMpgObject() {
		return this.mpgObject;
	}
	
	/**
	 * Get the name of the material as found in the IFC file
	 * @return
	 */
	public String getIfcName() {
		return this.ifcName;
	}

	/**
	 * get the name of the material as found in NMD
	 * @return a string with the nmd identifier
	 */
	public String getNmdIdentifier() {
		return nmdProductCard == null ? "" : nmdProductCard.getNLsfbCode();
	}

	/**
	 * the id of the material for internal BimBot use.
	 * @return a unique material identifier string
	 */
	public String getBimBotIdentifier() {
		return BimBotIdentifier;
	}
	
	/**
	 * set the BimBot ID 
	 * @param bimBotIdentifier - value to set the id to.
	 */
	public void setBimBotIdentifier(String bimBotIdentifier) {
		BimBotIdentifier = bimBotIdentifier;
	}
	
	public String print() {
		StringBuilder sb = new StringBuilder();
		sb.append("material : " + ifcName + " with properties" + System.getProperty("line.separator"));
		sb.append("NMD ID: " + getNmdIdentifier() + System.getProperty("line.separator"));
		sb.append("nmd material(s) linked to MpgMaterial: " + System.getProperty("line.separator"));
		sb.append("specs undefined " + System.getProperty("line.separator"));
		
		return sb.toString();
	}

	public NmdProductCard getNmdProductCard() {
		return nmdProductCard;
	}

	public void setProductCard(NmdProductCard productCard) {
		this.nmdProductCard = productCard;
		
		// check with the store which child elements will also be mapped with this action
	}
	
	public void removeProductCard() {
		// unmap any child elements.
	}

	public Double getRequiredNumberOfUnits() {
		if (this.mpgObject == null || this.getNmdProductCard() == null) {
			return Double.NaN;
		}
		
		String productUnit = this.getNmdProductCard().getUnit();
		if (productUnit == "m1") {
			return mpgObject.getVolume() / mpgObject.getArea();
		}
		if (productUnit == "m2") {
			return mpgObject.getArea();
		}
		if (productUnit == "m3") {
			return mpgObject.getVolume();
		}
		if (productUnit == "kg") {
			return Double.NaN;
		}
		
		return Double.NaN;
		
	}
}
