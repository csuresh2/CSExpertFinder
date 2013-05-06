import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;


public class PeopleDocuments extends Documents
{
	/**
	 * Set people documents path.
	 * @param documentsPath
	 */
	public PeopleDocuments(String documentsPath, SearchResult result)
	{
		super(documentsPath, result);
	}

	/**
	 * Index people documents.
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void index() throws SolrServerException, IOException
	{
		System.out.println("\nInitiating people indexing..\n");
		result.appendResultString("\nInitiating people indexing..\n");
		setCurrentCore("people");
		runIndexing();
		result.appendResultString("People indexing completed..\n");
		System.out.println("People indexing completed..\n");
	}
}
