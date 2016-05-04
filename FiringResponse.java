
public class FiringResponse {
	private FiringResult result;
	private LocationContentTypes content;
	
	public FiringResponse(FiringResult result, LocationContentTypes content) {
		this.result = result;
		this.content = content;
	}
	
	public FiringResult getResult() {
		return result;
	}
	public void setResult(FiringResult result) {
		this.result = result;
	}
	public LocationContentTypes getContent() {
		return content;
	}
	public void setContent(LocationContentTypes content) {
		this.content = content;
	}
	
	
}
