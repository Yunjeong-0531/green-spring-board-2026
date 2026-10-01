package com.green.spring_board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "boards")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Board {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Column(nullable = false)
   private String title;

   @Column(nullable = false)
   private String content;

   @Column(nullable = false)
   private int hits;

}
