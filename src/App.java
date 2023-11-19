import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class App extends JFrame 
{
  private JTextArea rdfText;
  private JButton generate;
  private JButton clear;
  private JLabel graphImage;

  public App()
  {
    initializeUI();
  }

  private void initializeUI()
  {
    setTitle("RDF Graph Generator");
    setSize(1500,1400);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    rdfText = new JTextArea(40, 40);
    JScrollPane scrollPane = new JScrollPane(rdfText);
    mainPanel.add(scrollPane);

    generate = new JButton("Generate Graph");
    generate.addActionListener(new ActionListener() 
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          generateGraph();
        }
    });

    clear = new JButton("Clear Text");
    clear.addActionListener(new ActionListener() 
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        rdfText.setText("");
      }
    });

    JPanel middle = new JPanel(new FlowLayout());
    middle.add(generate);
    middle.add(clear);
    mainPanel.add(middle);

    
    graphImage = new JLabel();
    graphImage.setPreferredSize(new java.awt.Dimension(1500,700));
    graphImage.setAlignmentX(CENTER_ALIGNMENT);
    mainPanel.add(graphImage);

    add(mainPanel);
    setVisible(true);
  }

  private void generateGraph()
  {
    String rdf = rdfText.getText();
    String graphImageUrl = sendRDFAndGetGraphImage(rdf);
    displayGraphImage(graphImageUrl);
  }

  private String sendRDFAndGetGraphImage(String rdf)
  {     
        String image = "";
  
        String url = "https://www.w3.org/RDF/Validator/"; //the web page that we want to perform web scraping on
  
        System.setProperty("webdriver.chrome.driver","src/drivers/chromedriver"); //set the path to chrome driver
        WebDriver driver = new ChromeDriver(); //initialize the web driver
        driver.get(url); //open the web page

        WebElement textarea = driver.findElement(By.name("RDF")); //find the textarea in the webpage
        textarea.clear(); //clear the textarea in case it already has text
        textarea.sendKeys(rdf); //send the text that the user enetered to the textarea in the webpage
        Select dropdown = new Select(driver.findElement(By.name("TRIPLES_AND_GRAPH"))); //find the dropdown list on the webpage
        dropdown.selectByIndex(1); //select the second option which let us generate an rdf graph and triples
        WebElement button = driver.findElement(By.cssSelector("input[value='Parse RDF']")); //find the button on the webpage used to parse the rdf
        button.click(); //click the button to prase rdf

        String script = "return document.body.textContent"; //here we want to check if there is any error,the error in webpage is not included in tags so we have to retrieve the whole textContent
        String text = (String) ((JavascriptExecutor) driver).executeScript(script); //we use javascript to execute the script

        //check if the return text contains error message or fatalerror
        if(text.contains("Error MessagesError") || text.contains("FatalError"))
        {
          int startIndex = text.indexOf("Error MessagesError"); //get the index of MessageError
      
          if(startIndex != -1) //if index exist
          { int endIndex = text.indexOf("\n", startIndex); //get the index of "\n" ie the end of the error message
            String extractedText = text.substring(startIndex, endIndex); //extract the text from Error MessageError to the end line
            JOptionPane.showMessageDialog(null, extractedText, "Error", JOptionPane.ERROR_MESSAGE); //show an error message
          }

          startIndex = text.indexOf("FatalError"); //get the index of FatalError
          if(startIndex != -1) //if index exist
          {
            int endIndex = text.indexOf("\n", startIndex); //get the index of "\n" ie the end of the fatal error message
            String extractedText = text.substring(startIndex, endIndex); //extract the text from FatalError to the end of line
            JOptionPane.showMessageDialog(null, extractedText, "Error", JOptionPane.ERROR_MESSAGE); //show an error message
          }
        }
        else{ //otherwise if there are no error, so the rdf is parsed 
          WebElement img = driver.findElement(By.cssSelector("img[alt='graph representation of RDF data']")); //retrieve the img tag
          image = img.getAttribute("src"); //get the source ie the link of the image
         
        }

    return image; //return the image link
  }

  private void displayGraphImage(String imageUrl) {
        ImageIcon imageIcon = null;
        try{
            if(!imageUrl.equals(""))
            {  
               imageIcon = new ImageIcon(new URL(imageUrl));
               graphImage.setIcon(imageIcon);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }

}

//for testing
/*<?xml version="1.0"?>
<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:uni="http://www.mydomain.org/uni-ns#">
  <rdf:Description rdf:about="949318">
    <uni:name>David Billington</uni:name>
    <uni:title>Associate Professor</uni:title>
    <uni:age rdf:datatype="xsd:integer;">27</uni:age> 
  </rdf:Description>
  <rdf:Description rdf:about="CIT1111">
    <uni:courseName>Discrete Maths</uni:courseName>
    <uni:isTaughtBy>David Billington</uni:isTaughtBy>
  </rdf:Description>
  <rdf:Description rdf:about="CIT2112">
    <uni:courseName>Programming III</uni:courseName>
    <uni:isTaughtBy>Michael Maher</uni:isTaughtBy>
  </rdf:Description>
</rdf:RDF> */