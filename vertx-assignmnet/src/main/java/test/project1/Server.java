package test.project1;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.text.Collator;
import java.util.*;

public class Server extends AbstractVerticle {

	private Router router;
	private TreeSet<String> wordslexically;
	private SortedSet<String> mySet;
	private String value;
	private String lexical;
	 private TreeMap<Integer , TreeSet<String>> valueStringMap;
	@Override
	public void start(Future<Void> fut) throws Exception {
		router = Router.router(vertx);
		Collator mycollator = Collator.getInstance(Locale.US);
		mycollator.setStrength(Collator.PRIMARY);
		wordslexically = new TreeSet<>(mycollator);
		valueStringMap = new TreeMap<>();

		router.route().handler(BodyHandler.create());


		router.post("/analyze").handler(ctx -> {

			JsonObject newWord = ctx.getBodyAsJson();
			String receivedWord =  newWord.getString("text");
			HttpServerResponse response = ctx.response();
			if(!wordslexically.contains(receivedWord)){
				wordslexically.add(receivedWord);
			}

			 String temp = receivedWord.toLowerCase();
			 int sum = 0;
			 for(int i=0; i<temp.length();i++){
			 		sum  = sum +  (Character.getNumericValue(temp.charAt(i))-9);
			}

			if(valueStringMap.containsKey(sum)){
			 	value = valueStringMap.get(sum).first();
				valueStringMap.get(sum).add(receivedWord);
				valueStringMap.put(sum, valueStringMap.get(sum));
			}
			else{
				mySet = new TreeSet<>();
				mySet.add(receivedWord);
				valueStringMap.put(sum, (TreeSet<String>) mySet);
				if(valueStringMap.higherKey(sum)== null &&  valueStringMap.lowerKey(sum)==null){
					value= null;
				} else if(valueStringMap.higherKey(sum)== null){
					value = valueStringMap.get(valueStringMap.lowerKey(sum)).first();

				}else if(valueStringMap.lowerKey(sum)==null){
					value = valueStringMap.get(valueStringMap.higherKey(sum)).first();

				}else{
						if( valueStringMap.higherKey(sum) - sum > sum - valueStringMap.lowerKey(sum)){
							value = valueStringMap.get(valueStringMap.lowerKey(sum)).first();

						}else if(valueStringMap.higherKey(sum) - sum < sum - valueStringMap.lowerKey(sum)){
							value = valueStringMap.get(valueStringMap.higherKey(sum)).first();
						}
				}
			}

					if(wordslexically.higher(receivedWord) == null && wordslexically.lower(receivedWord) == null){
							lexical=null;
					}
			else if(wordslexically.higher(receivedWord) != null && wordslexically.lower(receivedWord) == null ) {
				lexical = wordslexically.higher(receivedWord);
			}
			else if(wordslexically.lower(receivedWord) != null &&  wordslexically.higher(receivedWord) == null) {
				lexical = wordslexically.lower(receivedWord);
			}
			else {
						lexical = closestString(wordslexically.higher(receivedWord) ,receivedWord,wordslexically.lower(receivedWord));

					}
			response
				 .putHeader("Content/type", "application/json")
				 .end(new JsonObject()
									.put("value", value)
									.put("lexical", lexical)
									.encodePrettily());


		});
		vertx.createHttpServer().requestHandler(router::accept)
			.listen(
				config().getInteger("http.port", 8000),
				result -> {
					if (result.succeeded()) {
						fut.complete();
					} else {
						fut.fail(result.cause());
					}
				});
	}
	public String closestString(String higher , String input , String lower){
		String shortest = higher.length()< input.length()? higher : input;
		shortest = shortest.length()<lower.length() ? shortest : lower;
		int i=0;
		while (i<shortest.length()){
			if(higher.charAt(i)==input.charAt(i) && input.charAt(i)==lower.charAt(i)){
				i++;

			}
			else if(input.charAt(i)!=higher.charAt(i) && input.charAt(i)==lower.charAt(i)){
				return lower;
			}
			else if(input.charAt(i)==higher.charAt(i) && input.charAt(i)!=lower.charAt(i)){
				return higher;
			}
			else{
			return Math.abs(Character.getNumericValue(lower.charAt(i)) - Character.getNumericValue(input.charAt(i))) <
						 Math.abs(Character.getNumericValue(higher.charAt(i)) - Character.getNumericValue(input.charAt(i)))?
						 lower : higher;
			}
		}
		return input;
	}

}
