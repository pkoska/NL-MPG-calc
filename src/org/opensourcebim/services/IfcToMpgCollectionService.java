package org.opensourcebim.services;

import org.bimserver.bimbots.BimBotContext;
import org.bimserver.bimbots.BimBotsException;
import org.bimserver.bimbots.BimBotsInput;
import org.bimserver.bimbots.BimBotsOutput;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.plugins.PluginConfiguration;
import org.opensourcebim.ifccollection.MpgIfcObjectCollector;
import org.opensourcebim.ifccollection.MpgObjectStore;
import org.opensourcebim.mapping.NmdDataResolver;
import org.opensourcebim.mapping.NmdDataResolverImpl;
import org.opensourcebim.mapping.NmdMappingDataServiceImpl;
import org.opensourcebim.nmd.Nmd2DataService;

public class IfcToMpgCollectionService extends IfcObjectCollectionBaseService {

	@Override
	public BimBotsOutput runBimBot(BimBotsInput input, BimBotContext bimBotContext, PluginConfiguration pluginConfiguration)
			throws BimBotsException {

		IfcModelInterface ifcModel = input.getIfcModel();

		// Get properties from ifcModel
		MpgIfcObjectCollector matParser = new MpgIfcObjectCollector();
		MpgObjectStore ifcResults = matParser.collectIfcModelObjects(ifcModel);
		
		// resolve any ifc to nmd coupling
		NmdDataResolver resolver = new NmdDataResolverImpl();
		resolver.setNmdService(new Nmd2DataService(resolver.getConfig()));
		resolver.setMappingService(new NmdMappingDataServiceImpl(resolver.getConfig()));
		resolver.setStore(ifcResults);
		resolver.NmdToMpg();
				
		return this.toBimBotsJsonOutput(resolver.getStore(), "results object collection");
	}

	@Override
	public String getOutputSchema() {
		return "MPG_OBJECT_JSON_0_0_3";
	}
}
