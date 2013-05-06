import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;


public class ConceptDocuments extends Documents
{
	/**
	 * Set concept documents path.
	 * @param documentsPath
	 */
	public ConceptDocuments(String documentsPath, SearchResult result)
	{
		super(documentsPath, result);
	}

	/**
	 * Index concept documents.
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void index() throws SolrServerException, IOException
	{
		System.out.println("\nInitiating concept indexing..\n");
		result.appendResultString("\nInitiating concept indexing..\n");
		setCurrentCore("concept");
		runIndexing();
		result.appendResultString("Concept indexing completed..\n");
		System.out.println("Concept indexing completed..\n");
	}
}
