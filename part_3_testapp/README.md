# Kolomolo Java Test - Part 3

## Micro Data Warehouse with Docker, Spring boot, Spark and Hadoop

You will be required to approach a completely new problem, that you likely did not have the opportunity to work with before.
Your task is to understand the requirements of the project and follow up by learning and applying new skills.

The software - HADOOP, Spark and Docker - are not part of the job, but those tools and skills are very demanded on the market
and sooner or later you will have to deal with them. You will be tasked to build a small project that combines a set of skill
to achieve a specific result.

Please, provide us with the code solution to create a micro warehouse solution. For this purpose you will create a docker compose that runs SPARK and HADOOP toy data warehouse. If you find a better way than running _docker compose_, then do it. Result is what matters. You must be able to somehow upload the provided [dataset file](https://github.com/alexkolomolo/javatest/blob/main/data/foodhub_order.csv) to Hadoop remote filesystem. Also, use whatever means necessary and be able to explain why you chosen this way and how. You should be able to build a small __Spark__ application to generate a result from the dataset: 10 Top cuisine types along with the number of restaurants serving them. You should be able to build java springboot application to execute this __spark application__ and get the result in your springboot application. As a bonus you may display the results using simple JSP or any other method you deem suitable.

__Summary of the requirements__:
- docker images for Hadoop and Spark must run
- you must be able to upload dataset file to Hadoop
- Spark application must run on spark to process dataset
- Applicaiton will generate 10 top quisine types from the dataset
- you must be able to retrieve results from the processed dataset via java springboot
- (optional) you should be able to display the processed data (JSP)

### Submission rules

You should submit the code into your cloned repository and either give us access and share it with us or zip the repository and share
the project via email. Take the time to learn the new technology and solve the problem.
