package com.devs.rest.webservices.restfulwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devs.rest.webservices.restfulwebservices.jpa.PostRepository;
import com.devs.rest.webservices.restfulwebservices.jpa.UserRepository;

import jakarta.validation.Valid;

@RestController
public class UserJpaResource {

	private UserRepository repository;
	private PostRepository postRepository;

	public UserJpaResource(UserRepository userRepository, PostRepository postRepository) {
		this.repository = userRepository;
		this.postRepository = postRepository;
	}

	@GetMapping(path = "/jpa/users")
	public List<User> retriveAllUsers() {
		List<User> users = repository.findAll();
		return users;
	}

	
	@GetMapping(path = "/jpa/users/{id}")
	public EntityModel<User> retriveUser(@PathVariable int id) {
		Optional<User> user = repository.findById(id);

		if (user.isEmpty()) {
			throw new UserNotFoundException("Id: " + id);
		}
		EntityModel<User> entityModel = EntityModel.of(user.get());
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retriveAllUsers());
		WebMvcLinkBuilder linkPosts = linkTo(methodOn(this.getClass()).retrivePostsForUser(id));
		entityModel.add(link.withRel("all-users"));
		entityModel.add(linkPosts.withRel("posts"));
		return entityModel;
	}

	@PostMapping(path = "/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {

		User savedUser = repository.save(user);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping(path = "/jpa/users/{id}")
	public void deleteUser(@PathVariable int id) {
		repository.deleteById(id);
	}

	@GetMapping(path = "/jpa/users/{id}/posts")
	public List<Post> retrivePostsForUser(@PathVariable int id) {
		Optional<User> user = repository.findById(id);
		if (user.isEmpty()) {
			throw new UserNotFoundException("Id: " + id);
		}
		return user.get().getPosts();
	}
	
	@PostMapping(path = "/jpa/users/{id}/posts")
	public ResponseEntity<Post> createPostForUser(@PathVariable int id, @Valid @RequestBody Post post) {
		Optional<User> user = repository.findById(id);
		if (user.isEmpty()) {
			throw new UserNotFoundException("Id: " + id);
		}
		post.setUser(user.get());
		Post savedPost = postRepository.save(post);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedPost.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}
	
	@GetMapping(path = "/jpa/users/{id}/posts/{postId}")
	public EntityModel<Post> retrivePostsOfUser(@PathVariable int id, @PathVariable int postId) {
		Optional<User> user = repository.findById(id);

		if (user.isEmpty()) {
			throw new UserNotFoundException("Id: " + id);
		}
		Optional<Post> userPost = postRepository.findById(postId);
		if (user.isEmpty()) {
			throw new PostNotFoundException("Post Id: " + postId);
		}
		EntityModel<Post> entityModel = EntityModel.of(userPost.get());
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retriveAllUsers());
		WebMvcLinkBuilder linkPosts = linkTo(methodOn(this.getClass()).retrivePostsForUser(id));
		entityModel.add(link.withRel("all-users"));
		entityModel.add(linkPosts.withRel("posts"));
		return entityModel;
	}	
 
}
