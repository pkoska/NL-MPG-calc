package org.opensourcebim.ifccollection;

import java.util.Collection;
import java.util.List;

public interface MpgObject {
	
	long getObjectId();
	List<MpgSubObject> getSubObjects();
	String getObjectName();
	String getObjectType();
	String getGlobalId();
	String getParentId();
	void setParentId(String value);
	
	double getVolume();
	
	Collection<String> getListedMaterials();
	
	void addListedMaterial(String materialName, String GUID);

	void addSubObject(MpgSubObject mpgSubObject);
	
	String print();
	boolean hasDuplicateMaterialNames();


}
