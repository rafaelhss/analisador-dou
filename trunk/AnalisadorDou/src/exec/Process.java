package exec;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import dou.DouProcessor;
import dou.processador.ProcessadorInicioInicio;
import dou.processador.registro.RegistroSQLServer;

public class Process
{

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)
	{

		try
		{
			// Lista os arquivos a serem processados
			File arquivo = new File(args[1]);
			File gapp = new File(args[0]);

			System.out.println("Argumento [0] : " + args[0]);
			System.out.println("Argumento [1] : " + args[1]);

			// File[] arquivosPequenos = new Separador().separa(arquivo, TEMP_DIR);

			// for (File arquivoPequeno : arquivosPequenos) {

			File arquivoPequeno = arquivo;

			System.out.println(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date()) + "IOPA ###Process: "
					+ arquivo.getName() + "   size: " + arquivo.length() + "###arquivoPequeno: " + arquivoPequeno.getName()
					+ "   size: " + arquivoPequeno.length());

			if (arquivoPequeno.length() > 0)
			{
				try
				{

					new DouProcessor().processFile(gapp, arquivoPequeno, new ProcessadorInicioInicio(), new RegistroSQLServer());

					File newFile = new File(arquivoPequeno.getAbsolutePath().replace("\\txt\\", "\\ProcessedTxt\\"));

					File theDir = new File(newFile.getParent());
					if (!theDir.exists())
						theDir.mkdir();

					System.out.println("NewFile: " + newFile.getAbsolutePath());

					if (arquivoPequeno.renameTo(newFile))
					{
						System.out.println("File is moved successful!");
					} else
					{
						System.out.println("File is failed to move! ok  " + newFile.getAbsolutePath());
					}

				} catch (Exception e)
				{
					System.out.println("Exception: " + e.getMessage());
					System.out.println("Exce��o ao processar arquivo: " + arquivoPequeno.getName());

					try
					{

						if (arquivoPequeno.renameTo(new File("C:\\TempDirError\\" + arquivoPequeno.getName())))
						{
							System.out.println("File is moved successful!");
						} else
						{
							System.out.println("File is failed to move! ex " + arquivoPequeno.getAbsolutePath());
						}

					} catch (Exception ex)
					{
						System.out.println("Exception ao mover: " + ex.getMessage());

					}

				}
			} else
			{
				System.out.println("Size <= 0 :  Nothing to do here...");
			}

			// }
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());

		}

	}
}