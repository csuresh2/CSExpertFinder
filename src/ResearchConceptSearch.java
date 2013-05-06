import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
/**
 * Research concept search
 * @author chethans
 */

public class ResearchConceptSearch implements Command
{
	private Query query;

	public ResearchConceptSearch(Query query)
	{
		this.query = query;
	}

	public void execute() throws SolrServerException, IOException
	{
		query.runSearch();
	}
}
