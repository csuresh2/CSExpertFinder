import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class CommandArguments
{
	// Function Constants 
	// Index types - Mulicore Solr enabled
	public static final int INDEX_PEOPLE = 1;
	public static final int INDEX_CONCEPTS = 2;
	public static final int INDEX_ALL = 3;

	// Search functions
	public static final int RESEARCH_CONCEPT_SEARCH = 4;
	public static final int RESEARCH_TERMS_SEARCH = 5;

	// Invalid command constant
	public static final int INVALID_COMMAND = 0;

	String[] arguments;
	String queryString;
	Query query;
	SearchResult result;
	int functionType;
	Command command;
	PeopleDocuments peopleDocuments;
	ConceptDocuments conceptDocuments;
	
	// Research Concept files location - Research concept files contain top K terms
	// along with weights for each research concept. eg. Data Mining.
	String solrConceptsDocumentsPath;

	// If indexing, location of documents path. Each document is a person profile
	// extracted from web using focused on selected domains */
	String solrPeopleDocumentsPath;

	public CommandArguments(String[] arguments, SearchResult result) throws IOException
	{
		this.arguments = arguments;
		initializeDocumentsPath();
		parseArguments();
		identifyCommand();
	}

	public Query getQuery()
	{
		return query;
	}

	public Command getCommand()
	{
		return command;
	}

	void parseArguments()
	{
		int i = 0;
		String arg;

		// Parsing to identify the command
		while (i < arguments.length && arguments[i].startsWith("-"))
		{
			arg = arguments[i++];
			if (arg.equals("-csearch"))
			{
				functionType = RESEARCH_CONCEPT_SEARCH;
				// command = researchConceptSearchCommand;

				// add all remaining arguments as query string
				queryString = arguments[i++];
				while(i < arguments.length)
				{
					queryString += " ";
					queryString += arguments[i++];
				}
				if(queryString.isEmpty())
				{
					System.out.println("Empty research concept terms - Exiting");
					System.exit(-1);
				}
			}
			else if (arg.equals("-tsearch"))
			{
				functionType = RESEARCH_TERMS_SEARCH;
				// command = researchTermSearchCommand;

				// add all remaining arguments as query string
				queryString = arguments[i++];
				while(i < arguments.length)
				{
					queryString += " ";
					queryString += arguments[i++];;
				}
				if(queryString.isEmpty())
				{
					System.out.println("Empty query terms - Exiting");
					System.exit(-1);
				}
			}	            
			else if (arg.equals("-pindex"))
			{
				functionType = INDEX_PEOPLE;
				if(solrPeopleDocumentsPath.isEmpty())
				{
					// default path - from working directory
					solrPeopleDocumentsPath = System.getProperty("user.dir") + "/people_data";
				}
				peopleDocuments = new PeopleDocuments(solrPeopleDocumentsPath, result);
			}
			else if (arg.equals("-cindex"))
			{
				functionType = INDEX_CONCEPTS;
				if(solrConceptsDocumentsPath.isEmpty())
				{
					// default path - from working directory
					solrConceptsDocumentsPath = System.getProperty("user.dir") + "/concept_data";
				}
				conceptDocuments = new ConceptDocuments(solrConceptsDocumentsPath, result);
			}
			else if (arg.equals("-index"))
			{
				functionType = INDEX_ALL;

				if(solrConceptsDocumentsPath.isEmpty())
				{
					// default path - from working directory
					solrPeopleDocumentsPath = System.getProperty("user.dir") + "/people_data";
					solrConceptsDocumentsPath = System.getProperty("user.dir") + "/concept_data";
				}

				peopleDocuments = new PeopleDocuments(solrPeopleDocumentsPath, result);
				conceptDocuments = new ConceptDocuments(solrConceptsDocumentsPath, result);
			}
		}
	}

	public void identifyCommand()
	{
		switch(functionType)
		{
			case INDEX_PEOPLE:
				command = new IndexPeopleCommand(peopleDocuments);
				break;

			case INDEX_CONCEPTS:
				command = new IndexConceptsCommand(conceptDocuments);
				break;

			case INDEX_ALL:
				command = new IndexAllCommand(peopleDocuments, conceptDocuments);
				break;

			case RESEARCH_CONCEPT_SEARCH:
				query = new Query(queryString, result);
				command = new ResearchConceptSearch(query);
				break;

			case RESEARCH_TERMS_SEARCH:
				query = new TermQuery(queryString, result);
				command = new ResearchTermSearch(query);
				break;

			default:
				command = new InvalidCommand();
		}
	}

	void initializeDocumentsPath() throws IOException
	{
		Properties property = new Properties();
		String fileName = System.getProperty("user.dir") + "/CSExpertSearch.config";            
		InputStream is = new FileInputStream(fileName);
		property.load(is);

		// get all the config parameters
		solrPeopleDocumentsPath = property.getProperty("solr_people_documents_path");
		solrConceptsDocumentsPath = property.getProperty("solr_concepts_documents_path");
	}

	public SearchResult getResult()
	{
		return result;
	}
}
