package com.bootdo.signature.entity.vo;

import org.springframework.web.multipart.MultipartFile;

public class MultipartFileVO {
    private String id;

    private int chunk;

    private long size;

    private int chunks;

    private String type;

    private MultipartFile file;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getChunk() {
		return chunk;
	}

	public void setChunk(int chunk) {
		this.chunk = chunk;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}


	public int getChunks() {
		return chunks;
	}

	public void setChunks(int chunks) {
		this.chunks = chunks;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
    
}