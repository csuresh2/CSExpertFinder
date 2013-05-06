import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

/**
 * index concept command
 * @author chethans
 */
public class IndexConceptsCommand implements Command
{
	private ConceptDocuments docs;

	public IndexConceptsCommand(ConceptDocuments docs)
	{
		this.docs = docs;
	}

	public void execute() throws SolrServerException, IOException
	{
		docs.index();
	}
}
