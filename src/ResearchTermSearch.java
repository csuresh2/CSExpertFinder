import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
/**
 * Research term search
 * @author chethans
 */
public class ResearchTermSearch implements Command
{
	private Query query;

	public ResearchTermSearch(Query query)
	{
		this.query = query;
	}

	public void execute() throws SolrServerException, IOException
	{
		query.runSearch();
	}
}
