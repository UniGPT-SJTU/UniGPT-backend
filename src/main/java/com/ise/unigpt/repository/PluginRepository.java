package com.ise.unigpt.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ise.unigpt.model.Plugin;

@Repository
public interface PluginRepository extends JpaRepository<Plugin, Integer> {

    List<Plugin> findAllByOrderByIdDesc();
}
