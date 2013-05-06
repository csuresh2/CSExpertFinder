import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
/**
 * index people command
 * @author chethans
 */
public class IndexPeopleCommand implements Command
{
	private PeopleDocuments docs;

	public IndexPeopleCommand(PeopleDocuments docs)
	{
		this.docs = docs;
	}

	public void execute() throws SolrServerException, IOException
	{
		docs.index();
	}
}
