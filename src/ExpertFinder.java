import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServerException;

/**
 * The Class ExpertFinder
 * Main program for expert search
 */
public class ExpertFinder
{
	// Maximum number of concept queries generated
	public static int MAX_CONCEPT_QUERIES = 50;
	
	// Solr server home path
	public static String SOLR_URL;

	// Result file path
	public static String resultFilePath;
	
	public static String solrPeopleDocumentsPath;
	public static String solrConceptsDocumentsPath;

	public ExpertFinder() throws IOException
	{
		initializeProperties();
	}

	/**
	 * Load_from_config_file.
	 * Get parameters from config file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void initializeProperties() throws IOException
	{
		Properties property = new Properties();
		
		// The configuration filename.
		String fileName = System.getProperty("user.dir") + "/CSExpertSearch.config";           
		InputStream is = new FileInputStream(fileName);

		// Load the properties file.
		property.load(is);

		// initialize configuration parameters
		resultFilePath = property.getProperty("result_file_path");
		solrPeopleDocumentsPath = property.getProperty("solr_people_documents_path");
		solrConceptsDocumentsPath = property.getProperty("solr_concepts_documents_path");
		SOLR_URL = property.getProperty("SOLR_URL");
		MAX_CONCEPT_QUERIES = Integer.parseInt(property.getProperty("max_concept_queries"));
	}

	private void executeCommand(String[] arguments, SearchResult result) throws IOException, 
		SolrServerException
	{
		CommandArguments commArgs = new CommandArguments(arguments, result);
		Command command = commArgs.getCommand();

		// Perform requested function
		command.execute();
	}

	/**
	 * The main method.
	 * Entry method for all operations
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SolrServerException the solr server exception
	 */
	public static void main(String[] args) throws IOException, SolrServerException
	{
		try
		{
			// Initiate object for search/indexing
			ExpertFinder expertFinder = new ExpertFinder();

			SearchResult result = new SearchResult(resultFilePath);
			result.appendResultString("Computer Science Expert Finder from Web:\n");

			expertFinder.executeCommand(args, result);
			FileUtilities.stringToFile(result.getResultString(), result.getResultFilePath());
			System.out.println(result.getResultString());
		}
		catch(SolrServerException solrException)
		{
			System.out.println("Solr server exception message: " + solrException.getMessage());
		}
		catch(IOException ioex)
		{
			System.out.println("IO exception message: " + ioex.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
