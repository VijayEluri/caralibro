package caralibro.model.data.stream;

/* 
 * Comments are attached to posts and can only contain text (No photos, videos, likes, etc).
 * Can't be edited, only deleted if you have the right permissions.
 * Only users with an active session can comment.
 * 
 * @author		Federico Pascual Mastellone (fmaste@gmail.com)
 * @author		Simon Aberg Cobo (sima.cobo@gmail.com)
*/
public class Comment implements Stream {
	// It is composed of sourceId_postId_commentId
	private String id = null;
	private Long authorId = null;
	private String text = null;
	// Comments can't be updated and only have a creation time.
	private Long creationTime = null; // Unix time in seconds, not milliseconds
	private String permaLink = null;

	public Comment() {
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Long getAuthorId() {
		return authorId;
	}

	@Override
	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	public Long getCreationTime() {
		return creationTime;
	}

	/*
	 * @return Unix time in seconds, not milliseconds
	 */
	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
	}
	
	public String getPermaLink() {
		return permaLink;
	}

	public void setPermaLink(String permaLink) {
		this.permaLink = permaLink;
	}
	
}
