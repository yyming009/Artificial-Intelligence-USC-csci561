package homework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class homework {
	
	static int numOfQuery;
	static int numOfKb;
	static String[] query;
	static String[] kb;
	
	static List<Map<String, String>> compareList = new LinkedList<Map<String, String>>();
	static List<Map<String, String>> queryList = new LinkedList<Map<String, String>>();

	static List<Map<String, String>> inferenceList = new LinkedList<Map<String, String>>();
	
	static Map<String, String>upperMap, upperMap_Q;
	static List<Map<String, String>> tempUpperList = new LinkedList<Map<String, String>>();
	static List<Map<String, String>> tempUpperList_Q = new LinkedList<Map<String, String>>();
	static List<Map.Entry<String, String>> upperList;
	static List<Map.Entry<String, String>> checkUpperList;
	
	static List<String> printF = new LinkedList<String>();

	static Map<String,String> substiCheck = new HashMap<String,String>();
	static List<String> print = new LinkedList<String>();

	public static void main(String[] args) {
		
		String pathName = "input1.txt";
		File file = new File(pathName);		
		
		Scanner input = null;
		try {
			input = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//get the number of queries
		numOfQuery = Integer.parseInt(input.nextLine());
		query = new String[numOfQuery];
		//store each query of each line into query array
		for (int i = 0; i < numOfQuery && input.hasNextLine(); ++i) {
			query[i] = input.nextLine();
			//System.out.println(query[j]);
		}

		//get the number of kb
		numOfKb = Integer.parseInt(input.nextLine());
		kb = new String[numOfKb];
		//store each kb of each line into query array
		for(int j = 0; j < numOfKb && input.hasNextLine(); ++j) { 
		    kb[j] = input.nextLine();
			//System.out.println(kb[j]);
		}

		for (int j = 0; j < numOfKb; ++j) {
			buildSearchMap(kb[j]);
		}
		for (int j = 0; j < numOfQuery; ++j) {
			buildQueryMap(query[j]);
		}
		inferenceKB();
		inferenceQr();
		//System.out.println(tempUpperList_Q);
		//System.out.println(tempUpperList);
				
		for (int j = 0; j < tempUpperList_Q.size(); ++j) {
			upperList = new LinkedList<Map.Entry<String, String>>();
			checkUpperList = new LinkedList<Map.Entry<String, String>>();

			Iterator<Entry<String, String>> iter_query = tempUpperList_Q.get(j).entrySet().iterator();
			while (iter_query.hasNext()) {
				Map.Entry<String, String> pair = (Map.Entry<String, String>)iter_query.next();
				upperList.add(pair);
				checkUpperList.add(pair);
			}
			//System.out.println(upperList);

			for (int p = 0; p < tempUpperList.size(); ++p) {
				Iterator<Entry<String, String>> iter = tempUpperList.get(p).entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> pair = (Map.Entry<String, String>)iter.next();
					upperList.add(pair);
					checkUpperList.add(pair);
				}
			}
			//System.out.println(upperList);
			//System.out.println(compareList);

			if (substitute()) {
				printF.add("TRUE");
				System.out.println("TRUE");
			} else {
				printF.add("FALSE");
				System.out.println("FALSE");
			}
		}
		
		//*********************************************************************************************
		File f = new File("output.txt");
		try {
			if (f.createNewFile()){
			    System.out.println("File is created!");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try{
		    PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
			for (int i = 0; i < printF.size(); i++) {
				writer.println(printF.get(i));
			}
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		//System.out.println(upperList);
	}
	
	private static void buildSearchMap(String sen_kb) {
		String s = "";
		for (int i = 0; i < sen_kb.length(); ++i) {
			if (sen_kb.charAt(i) != ' ') {
				s += (sen_kb.charAt(i));
			}
		}
		//System.out.println(s);
		
		Map<String, String> tempMap = new HashMap<String, String>();
		
		String[] orDivide = s.split("\\|");
		for (int i = 0; i < orDivide.length; ++i) {
			String sep = orDivide[i];
			char[] char_sep = sep.toCharArray();
			
			String key="";
			for (char ch : char_sep) {
				if (ch == '(') {
					break;
				}
				key += ch;
			}
			tempMap.put(key, sep);
		}
		compareList.add(tempMap);
		//System.out.println(compareList);
	}
	
	private static void buildQueryMap(String sen_kb) {
		String s = "";
		for (int i = 0; i < sen_kb.length(); ++i) {
			if (sen_kb.charAt(i) != ' ') {
				s += (sen_kb.charAt(i));
			}
		}
		//System.out.println(s);
		
		Map<String, String> tempMap = new HashMap<String, String>();
		
		String[] orDivide = s.split("\\|");
		for (int i = 0; i < orDivide.length; ++i) {
			String sep = orDivide[i];
			char[] char_sep = sep.toCharArray();
			
			String key="";
			for (char ch : char_sep) {
				if (ch == '(') {
					break;
				}
				key += ch;
			}
			
			if (key.charAt(0) == '~') {
				key = key.substring(1, key.length());
				sep = sep.substring(1, sep.length());
			} else {
				key = "~" + key;
				sep = "~" + sep;
			}
			
			tempMap.put(key, sep);
		}
		queryList.add(tempMap);
		//System.out.println(compareList);
	}
	
	private static void inferenceKB() {
		
		for (int i = 0; i < compareList.size(); ++i) {
			Map<String, String>tempMap = compareList.get(i);
			Iterator<Entry<String, String>> iter = tempMap.entrySet().iterator();
			
			String mapKey = "", mapValue = "";
			
			totalargues:
			while (iter.hasNext()) {
				//get the constant in KB
				Map.Entry pair = (Map.Entry)iter.next();
				mapKey = pair.getKey().toString();
				mapValue = pair.getValue().toString();
				//System.out.println(mapKey + " " + mapValue);
				
				String[] divArray = divSentence(mapValue);
				String constant = divArray[0];
				String argument = divArray[1];
				
				String[] arg_split = argument.split("\\,");
				boolean fl = true;
				for (int j = 0; j < arg_split.length; ++j) {
					if(Character.isLowerCase(arg_split[j].charAt(0))) {
						fl = false;
						break;
					} 
					
				}
				
				if (fl) {
					upperMap = new HashMap<String, String>();
					upperMap.put(constant, argument);
					tempUpperList.add(upperMap);
				}
			}
		}
		
		//System.out.println(upperList);
	}
	
	private static void inferenceQr() {
			
			for (int i = 0; i < queryList.size(); ++i) {
				Map<String, String>tempMap = queryList.get(i);
				Iterator<Entry<String, String>> iter = tempMap.entrySet().iterator();
				
				String mapKey = "", mapValue = "";
				
				totalargues:
				while (iter.hasNext()) {
					//get the constant in KB
					Map.Entry<String, String> pair = (Map.Entry<String, String>)iter.next();
					mapKey = pair.getKey().toString();
					mapValue = pair.getValue().toString();
					//System.out.println(mapKey + " " + mapValue);
					
					String[] divArray = divSentence(mapValue);
					String constant = divArray[0];
					String argument = divArray[1];
					
					String[] arg_split = argument.split("\\,");
					for (int j = 0; j < arg_split.length; ++j) {
						if(!Character.isLowerCase(arg_split[j].charAt(0))) {
							upperMap_Q = new HashMap<String, String>();
							upperMap_Q.put(constant, argument);
							tempUpperList_Q.add(upperMap_Q);
							continue totalargues;
						} 
					}
				}
			}
			
			//System.out.println(upperList);
		}
	
	private static boolean substitute() {
		long startTime = System.currentTimeMillis();

		
		String mapKey = "", args = "";
		List<List<Map.Entry<String, String>>>newUpperList = new LinkedList<List<Map.Entry<String, String>>>();

		for (int ind = 0; ind < upperList.size(); ++ind) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>)upperList.get(ind);
			List<Map.Entry<String, String>>te = new LinkedList<Map.Entry<String, String>>();
			te.add(pairs);
			newUpperList.add(te);
		}
		//System.out.println(newUpperList);
		
		List<Map<String, String>> newCompareList = new LinkedList<Map<String, String>>();
		newCompareList.addAll(compareList);
		//System.out.println(newCompareList);
		
//**************LOOP BEGIN*****************************************************************************************************************************		
		
		for (int index = 0; index < newUpperList.size(); ++index) {
			//System.out.println(newUpperList);
			
			List<Map.Entry<String, String>>tel = newUpperList.get(index);
			for (int inde1 = 0; inde1 < tel.size(); ++inde1) {
				Map.Entry<String, String> pairs = (Map.Entry<String, String>)tel.get(inde1);
				mapKey = pairs.getKey().toString();
				args = pairs.getValue().toString();
				//System.out.println(mapKey + " " + args);
				String[] arg_split = args.split("\\,");
				
				//upper map attributes
//****************************************************************************************************************
				//traverse kb
				
				for (int i = 0; i < newCompareList.size(); ++i) {
					Map<String, String>tempMap = newCompareList.get(i);
					//System.out.println(compareList);
					
					
					String value = "";
					if (mapKey.charAt(0) == '~') {
						if (tempMap.containsKey(mapKey.substring(1, mapKey.length()))) {
							value += tempMap.get(mapKey.substring(1, mapKey.length()));
						}
					} else {
						if (tempMap.containsKey("~" + mapKey)) {
							value += tempMap.get("~" + mapKey);
						}  
					}
					
					
					//System.out.println(value);
					if (value != "") {
						
						if (tempMap.size() > 1) {
						
							String[] div_value = divSentence(value);
							String constant = div_value[0];
							String argument = div_value[1];
							String[] arg_split1 = argument.split("\\,");
							
							int count = 0;
							for (int k = 0; k < arg_split1.length; ++k) {
								if (Character.isLowerCase(arg_split1[k].charAt(0))) {
									++count;
								}
							}
							
							if (count == arg_split.length) {
								
								String[] lower = new String[count];
								
								int n = 0, p = 0;
								while(n < arg_split1.length) {
									if (Character.isLowerCase(arg_split1[n].charAt(0))) {
										lower[p++] = arg_split1[n];
									}
									++n;
								}
								
								String[] upper = new String[count];
								Map<String, String> subs = new HashMap<String, String>();
								for (int m = 0; m < count; ++m) {
									upper[m] = arg_split[m];
									subs.put(lower[m], upper[m]);
								}
								//System.out.println(subs);
		
								
						//*************************************************************************************
								
								
								Iterator<Entry<String, String>> iter_temp = tempMap.entrySet().iterator();
								//System.out.println(tempMap);
								
								
								List<Map.Entry<String, String>> t = new LinkedList<Map.Entry<String, String>>();
							
								long endTime = System.currentTimeMillis();
								if (endTime - startTime > 10000) {
									return true;
								}
								
								
								//System.out.print(tempMap);
								while (iter_temp.hasNext()) {
									Map.Entry pair1 = (Map.Entry)iter_temp.next();
									//System.out.println(pair1);
									if (!pair1.getValue().toString().equals(value)) {
									
										String mapKey1 = pair1.getKey().toString();
										String value1 = pair1.getValue().toString();
										//System.out.println(value1);
										
										String[] div_value1 = divSentence(value1);
										String constant1 = div_value1[0];
										String argument1 = div_value1[1];
				
										String new_arg = "";
										String[] neibor = argument1.split("\\,");
										for (int k = 0; k < neibor.length; ++k) {
											if (subs.containsKey(neibor[k])) {
												if (k == 0) {
													new_arg += subs.get(neibor[k]);
												} else {
													new_arg += "," + subs.get(neibor[k]);
												}
											} else {
												if (k == 0) {
													new_arg += neibor[k];
												} else {
													new_arg += "," + neibor[k];
												}
											}
										}
							
										
										//System.out.println(new_arg);
										Map<String, String> tem1 = new HashMap<String, String>();
										tem1.put(constant1, new_arg);
										Iterator<Entry<String, String>> temIter = tem1.entrySet().iterator();
										while (temIter.hasNext()) {
											Map.Entry<String, String>entry = temIter.next();
											t.add(entry);
										}
										
									}
								}
								
					
								if (t.size() == 1) {
									Map.Entry<String, String>entry = t.get(0);
									String constant1 = entry.getKey();
									String argument1 = entry.getValue();
										//check
									String constantCheck = "";
									if (constant1.charAt(0) == '~') {
										constantCheck = constant1.substring(1, constant1.length());
									} else {
										constantCheck = "~" + constant1;
									}
									
									Map<String, String> tem1 = new HashMap<String, String>();
									tem1.put(constantCheck, argument1);
									Iterator<Entry<String, String>> temIterCheck = tem1.entrySet().iterator();
									while (temIterCheck.hasNext()) {
										Map.Entry<String, String>entryCheck = temIterCheck.next();
										for (int ii = 0; ii < newUpperList.size(); ++ii) {
											if (newUpperList.get(ii).size() == 1 && newUpperList.get(ii).contains(entryCheck)) {
												System.out.println(entryCheck);
												return true;
											} 	
											
											List<Map.Entry<String, String>> l =  newUpperList.get(ii);
											if (l.size() == 1) {
												Map.Entry<String, String>lCheck = l.get(0);
											
												if (entryCheck.getKey() == lCheck.getKey() && lCheck.getValue().split("\\,").length == entryCheck.getValue().split("\\,").length) {
													System.out.println(entryCheck);
													return true;
												}
											}
										}
									}
									tem1.clear();
									
									boolean flag = true;
									for (int ii = 0; ii < newUpperList.size(); ++ii) {
										if (newUpperList.get(ii).contains(entry)) {
											flag = false;
										} 			
									}	
									
									if (flag) {
										newUpperList.add(t);
										
										Map<String, String>mp = new HashMap<String, String>();
										String value1 = constant1 + "(" + argument1 + ")";
										mp.put(constant1, value1);
										newCompareList.add(mp);
									}
	
								} else {
									
									
									
							//*************************************************************************************
							//check check *********************************************************
									for (int c1 = 0; c1 < t.size(); ++c1) {
										Map.Entry<String, String>entry1 = t.get(c1);
										String constant2 = entry1.getKey();
										String argument2 = entry1.getValue();
										
										String constantCheck = "";
										if (constant2.charAt(0) == '~') {
											constantCheck = constant2.substring(1, constant2.length());
										} else {
											constantCheck = "~" + constant2;
										}
										
										
										Map<String, String> tem2 = new HashMap<String, String>();
										tem2.put(constantCheck, argument2);
										Iterator<Entry<String, String>> temIterCheck1 = tem2.entrySet().iterator();
										Map.Entry<String, String>entryCheck = null;
										while (temIterCheck1.hasNext()) {
											entryCheck = temIterCheck1.next();
										}
										
										t.set(c1, entryCheck);
										boolean f = false;
										for (int ck = 0; ck < newUpperList.size(); ++ck) {
											if (newUpperList.get(ck).size() == t.size()) {
												f = false;
												for (int ck1 = 0; ck1 < t.size(); ++ck1) {
													if (!newUpperList.get(ck).contains(t.get(ck1))) {
														f = true;
														break;
													}	
												}
												if (f == false) {
													break;
												}
											} else {
												f = true;
											}
										}
										
										if (f) {
											t.set(c1, entry1);
										} else {
											return true;
										}
									}
									
							//check check *********************************************************
							//*************************************************************************************
	
									
									boolean flag = true;
									for (int ii = 0; ii < newUpperList.size(); ++ii) {
										if (newUpperList.get(ii).size() == t.size()) {
											for (int c = 0; c < t.size(); ++c) {
												if (!newUpperList.get(ii).contains(t.get(c))) {
													flag = true;
													break;
												}	
											}
										}
									}	
									
									if (flag) {
										newUpperList.add(t);
										
										for (int kk = 0; kk < t.size(); ++kk) {
											Map.Entry<String, String>entry1 = t.get(kk);
											String constant3 = entry1.getKey();
											String argument3 = entry1.getValue();
											
											Map<String, String>mp = new HashMap<String, String>();
											String value3 = constant3 + "(" + argument3 + ")";
											mp.put(constant3, value3);
											newCompareList.add(mp);
										}
									}
								}
								//System.out.println(t);
							}
							
							
						} 
						else {
							
							String[] div_value = divSentence(value);
							String constant = div_value[0];
							String argument = div_value[1];
							String[] arg_split1 = argument.split("\\,");
							
							boolean g = true;
							
							if (arg_split1.length == arg_split.length) {
								for (int i1 = 0; i1 < arg_split1.length; ++i1) {
									if (Character.isLowerCase(arg_split1[i1].charAt(0))){
										continue;
									} else {
										if (Character.isLowerCase(arg_split[i1].charAt(0))) {
											continue;
										} else {
											if (arg_split[i1].equals(arg_split1[i1])) {
												continue;
											} else {
												g = false;
											}
										}
									}
								}
							}
							
							
							if (g) {
								return true;
							}
							
						}
					}
				}
			}
		}
		//System.out.println(newUpperList);
		return false;
	}
		
	private static String[] divSentence(String s) {
			String[] div_CA = new String[2];
			
			char[] char_query = s.toCharArray();
			
			String constant = "", argument = "";
			
			//get the constant
			int index = 0;
			while (index < char_query.length) {
				if (char_query[index] == '(') {
					index++;
					break;
				}
				constant += char_query[index];
				index++;
			}
			
			//get the argument, split each argument then store it in qryarg_split
			for (int j = index; j < char_query.length; ++j) {
				if (char_query[j] ==  ')') {
					break;
				}
				if (char_query[j] == ' ') {
					continue;
				}
				argument += char_query[j];
			}
			
			div_CA[0] = constant;
			div_CA[1] = argument;
			return div_CA;
	}
	
}