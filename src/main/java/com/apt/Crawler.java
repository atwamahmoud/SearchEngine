package com.apt;

import java.util.*;


public class Crawler {

    public static ArrayList<String> links = SeedListGenerator.getSeedLinks();
    private static Counter counter = new Counter(0);
    private static boolean finishedSeed = false;
    private static ArrayList<String> unprocessedLinks = new ArrayList<>();
    private static ArrayList<String> processedLinks = new ArrayList<String>();

    public static String getAvailableLink() {
            if(finishedSeed){
                counter.check(unprocessedLinks.size());
                String link = unprocessedLinks.get(counter.getCount() - links.size() + 1);
                counter.increment();
                return link;
            }else{
                if(links.size() < counter.getCount()+1){
                    finishedSeed = true;
                    return Crawler.getAvailableLink();
                }
                String link = links.get(counter.getCount());
                counter.increment();
                return link;
            }
    }

    public static void addUnprocessedLinks(ArrayList<String> links) {
        synchronized (unprocessedLinks){
            unprocessedLinks.addAll(links);
            unprocessedLinks = Utils.removeDuplicates(unprocessedLinks);
            counter.uncheck();
        }
    }

    public static void addProcessedLink(String link) {
        synchronized (processedLinks){
            processedLinks.add(link);
        }
    }

    public static void main(String[] args) {

        int numThreads = Integer.parseInt(Utils.getUserInput("Number of threads: "));
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(new SeedWorker());
            threads.add(thread);
            threads.get(i).start();
        }

        //Persistence...
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHandler()));

    }
}
