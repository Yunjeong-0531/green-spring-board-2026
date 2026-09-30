package com.green.spring_board;

import jakarta.persistence.*;
import org.hibernate.annotations.Cache;

@Entity
@Table(name = "boards")
public class Boards {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Column(nullable = false)
   private String title;

   @Column(nullable = false)
   private String content;

   public Boards() {}

   public Boards(int id, String content, String title) {
      this.id = id;
      this.content = content;
      this.title = title;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }
}
