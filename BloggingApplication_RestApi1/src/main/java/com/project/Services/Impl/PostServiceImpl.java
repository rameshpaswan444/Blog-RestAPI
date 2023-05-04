package com.project.Services.Impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.Repositories.CategoryRepo;
import com.project.Repositories.PostRepo;
import com.project.Repositories.UserRepo;
import com.project.Services.PostService;
import com.project.entities.Category;
import com.project.entities.Post;
import com.project.entities.User;
import com.project.exceptions.ResourceNotFoundException;
import com.project.payloads.PostDto;
import com.project.payloads.PostResponse;


@Service
public class PostServiceImpl implements PostService {
	
	
	@Autowired
	private PostRepo postRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private CategoryRepo categoryRepo;
	
	@Override
	public PostDto createPost(PostDto postDto,Integer userId,Integer categoryId) {
		
User user=this.userRepo.findById(userId).orElseThrow(()->new ResourceNotFoundException("User", "User id",userId ));
		
		Category category=this.categoryRepo.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","category id",categoryId ));
		
		
		
		Post post=this.modelMapper.map(postDto, Post.class);
		post.setImageName("default.png");
		post.setAddedDate(new Date());
		post.setUser(user);
		post.setCategory(category);
		
		Post newPost=this.postRepo.save(post);
		
		return this.modelMapper.map(newPost, PostDto.class);
	}

	@Override
	public PostDto updatePost(PostDto postDto, Integer postId) {
		Post post=this.postRepo.findById(postId).orElseThrow(()->new ResourceNotFoundException("Post","post id",postId));
		post.setTitle(postDto.getTitle());
		post.setContent(postDto.getContent());
		post.setImageName(postDto.getImageName());
		Post updatedPost=this.postRepo.save(post);
		return this.modelMapper.map(updatedPost, PostDto.class);
	}

	@Override
	public void deletePost(Integer postId) {
		Post post=this.postRepo.findById(postId).orElseThrow(()->new ResourceNotFoundException("Post","post id",postId));
		this.postRepo.delete(post);
	}

	@Override
	public PostResponse getAllPost(Integer pageNumber,Integer pageSize,String sortBy,String sortDir) {
		
		Sort sort=null;
		if(sortDir.equalsIgnoreCase("asc"))
		{
			sort=Sort.by(sortBy).ascending();
		}else
		{
			sort=Sort.by(sortBy).descending();
		}
		Pageable p=PageRequest.of(pageNumber, pageSize,sort);
		
Page<Post>pagePost=	this.postRepo.findAll(p);
List<Post> allPosts=pagePost.getContent();
		List<PostDto>postDtos=allPosts.stream().map((post)->this.modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
		PostResponse postResponse=new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(pagePost.getNumber());
		postResponse.setPageSize(pagePost.getSize());
		
		postResponse.setTotalElements(pagePost.getTotalElements());
		postResponse.setTotalPages(pagePost.getTotalPages());
		postResponse.setLastPage(pagePost.isLast());
		
		return postResponse;
	}

	@Override
	public PostDto getPostById(Integer postId) {
		Post post=this.postRepo.findById(postId).orElseThrow(()->new ResourceNotFoundException("Post","post iD",postId));
		return this.modelMapper.map(post, PostDto.class);
	}

	@Override
	public List<PostDto> getPostByCategory(Integer categoryId) {
		Category cat=this.categoryRepo.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","category id",categoryId));
	List<Post> posts=this.postRepo.getByCategory(cat);
	
	List<PostDto> postDtos=posts.stream().map((post)->this.modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
	return postDtos;
	}

	@Override
	public List<PostDto> getPostByUser(Integer userId) {
		User user=this.userRepo.findById(userId).orElseThrow(()->new ResourceNotFoundException("User","userId",userId));
		
		List<Post> posts=this.postRepo.getByUser(user);
		List<PostDto>postDtos=posts.stream().map((post)->this.modelMapper.map(posts, PostDto.class)).collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public List<PostDto> searchPosts(String keyword) {
		List<Post> posts=this.postRepo.findByTitleContaining(keyword);
		List<PostDto> postDtos=posts.stream().map((post)->this.modelMapper.map(posts, PostDto.class)).collect(Collectors.toList());
		
		
		
		return postDtos;
	}

}