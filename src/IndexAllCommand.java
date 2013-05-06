import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

/**
 * Index all command
 * @author chethans
 */
public class IndexAllCommand implements Command
{
	private PeopleDocuments peopleDocs;
	private ConceptDocuments conceptDocs;

	public IndexAllCommand(PeopleDocuments peopleDocs, ConceptDocuments conceptDocs)
	{
		this.peopleDocs = peopleDocs;
		this.conceptDocs = conceptDocs;
	}

	public void execute() throws SolrServerException, IOException
	{
		peopleDocs.index();
		conceptDocs.index();
	}
}
