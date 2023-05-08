package com.devs.springboot.todowebapp.todo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
public class TodoService {

	private static List<Todo> todos = new ArrayList<>();
	private static int todosCount = 0;
	static {
		todos.add(new Todo(++todosCount, "devs", "learn AWS", 
				LocalDate.now().plusYears(1), false));
		
		todos.add(new Todo(++todosCount, "devs", "learn DevOps", 
				LocalDate.now().plusYears(2), false));
		
		todos.add(new Todo(++todosCount, "devs", "learn Fullstack Development", 
				LocalDate.now().plusYears(3), false));
	}
	
	public List<Todo> findByUsername(String username){
		return todos;
	}
	
	public void addTodo(String username, String description, LocalDate targetDate, boolean done) {
		Todo todo = new Todo(++todosCount, username, description, targetDate, done);
		todos.add(todo);
	}
	
	public void deleteById(int id) {
//		todos.remove(id-1);
		Predicate<? super Todo> predicate = 
				todo -> todo.getId() == id;
		todos.removeIf(predicate);
		System.out.println(todos.toString());
//		todos.add(todo);
	}

	public Todo findById(int id) {
		System.out.println("ID:"+id);
		// TODO Auto-generated method stub
		Predicate<? super Todo> predicate = 
				todo -> todo.getId() == id;
		Todo todo = todos.stream().filter(predicate).findFirst().get();
		return todo;
	}

	public void upadteTodo(@Valid Todo todo) {
		// TODO Auto-generated method stub
		deleteById(todo.getId());
		todos.add(todo);
	}
}
