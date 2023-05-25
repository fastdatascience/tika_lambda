package harmony;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.tika.Tika;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;

// Handler value: example.HandlerString
public class HandlerPDF implements RequestHandler<List<Map<String, Object>>, List<Map<String, Object>>>{

  @Override
  /*
   * Takes a String as input, and converts all characters to lowercase.
   */
  public List<Map<String, Object>> handleRequest(List<Map<String, Object>> event, Context context)
  {
    LambdaLogger logger = context.getLogger();
    logger.log("EVENT TYPE: " + event.getClass().toString());
    logger.log("EVENT BODY: " + event.get(0).get("content").toString());
    logger.log("EVENT BODY TYPE: " + event.get(0).get("content").getClass().toString());

    try {
      byte[] decodedString = Base64.getDecoder().decode(event.get(0).get("content").toString().getBytes("UTF-8"));
      InputStream stream = new ByteArrayInputStream(decodedString);

      BodyContentHandler handler = new BodyContentHandler();
      Metadata metadata = new Metadata();
      ParseContext parseContext = new ParseContext();
      PDFParser pdfparser = new PDFParser();
      pdfparser.parse(stream,handler,metadata,parseContext);

      event.get(0).put("text_content", handler.toString());

      return event;

    } catch (Exception exception) {
      logger.log("Something went wrong.");
      logger.log("Your description here " + exception.toString());
      List<Map<String, Object>> errorResponse = new ArrayList<Map<String, Object>>();
      errorResponse.add(new HashMap<String, Object>());
      errorResponse.get(0).put("Error", exception.toString());
      return errorResponse;
    }

  }
}