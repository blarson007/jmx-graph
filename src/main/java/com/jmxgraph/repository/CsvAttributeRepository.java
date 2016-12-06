package com.jmxgraph.repository;

@Deprecated // Let's not use CSV
public class CsvAttributeRepository /** implements JmxAttributeRepository, AutoCloseable */ {
	
//	private CSVWriter csvWriter;
//	
//	public CsvAttributeRepository(Date currentDate) {
//		try {
//			csvWriter = new CSVWriter(new FileWriter("jmx_" + new SimpleDateFormat("MM_dd_yyyy_kk_mm_ss").format(currentDate) + ".csv"));
//			csvWriter.writeNext(new String[] { "Object Name", "Attribute", "Value", "Path", "Timestamp" } );
//			csvWriter.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Override
//	public void saveJmxAttributeValue(JmxAttributeValue jmxAttributeValue) throws IOException {
//		String[] values = { 
//				jmxAttributeValue.getObjectName(), 
//				jmxAttributeValue.getAttribute(),
//				jmxAttributeValue.getPath(),
//				String.valueOf(jmxAttributeValue.getAttributeValue()), 
//				new SimpleDateFormat("MM/dd/yyyy mm:ss").format(jmxAttributeValue.getTimestamp()) 
//		};
//		csvWriter.writeNext(values);
//		csvWriter.flush();
//	}
//
//	@Override
//	public void close() throws IOException {
//		csvWriter.close();
//	}
//
//	@Override
//	public Collection<JmxAttributeValue> getAllJmxAttributeValues() {
//		throw new NotImplementedException("Get All JMX Attributes not implemented for CSV Repository");
//	}
}
