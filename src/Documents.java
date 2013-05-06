import java.io.File;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;


public class Documents
{
	protected String documentsPath;
	protected String currentCore;
	protected SearchResult result;

	public Documents(String documentsPath, SearchResult result)
	{
		this.documentsPath = documentsPath;
		this.currentCore = "people";
		this.result = result;
	}

	/**
	 * Sets currentCore for Solr server initialization.
	 * @param currentCore
	 */
	public void setCurrentCore(String currentCore)
	{
		this.currentCore = currentCore;
	}

	/**
	 * Index_documents.
	 * Index the documents by reading the files from the specified location 
	 * @param documentsPath the documents path
	 * @throws SolrServerException the solr server exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void indexDocuments() throws SolrServerException, IOException
	{
		File[] files = FileUtilities.getFileListInDir(documentsPath);
		String person_name;
		String file_content;
		int i= 0;
		// Add each document to the index
		HttpSolrServer server = new HttpSolrServer(ExpertFinder.SOLR_URL + currentCore);

		// Delete current index
		server.deleteByQuery( "*:*" );
		for(File file:files)
		{
			person_name = file.getName();
			file_content = FileUtilities.loadStringFromFile(file);
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("cat", person_name);
			doc.addField("id",  person_name);
			doc.addField("name", person_name);
			doc.addField("content",file_content);
			server.add(doc);
			i++;

			// periodically flush
			if(i%100 == 0)
				server.commit();
		}
		server.commit(); 
	}

	/**
	 * Run_indexing.
	 * Create solr index
	 * @param path the path
	 * @throws SolrServerException the solr server exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void runIndexing() throws SolrServerException, IOException
	{
		indexDocuments();
	}
}