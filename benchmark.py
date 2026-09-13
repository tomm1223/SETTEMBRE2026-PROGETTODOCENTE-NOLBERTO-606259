import sys
import json

try:
    import requests
except ImportError:
    print("ERRORE: La libreria 'requests' non e' installata.")
    print("Installala eseguendo: pip install requests matplotlib")
    sys.exit(1)

try:
    import matplotlib.pyplot as plt
except ImportError:
    print("ERRORE: La libreria 'matplotlib' non e' installata.")
    print("Installala eseguendo: pip install matplotlib")
    sys.exit(1)

def run_single_benchmark():
    user_input = input("\nInserisci il numero di film dummy da simulare [Default 100]: ").strip()
    count = 100
    if user_input.isdigit() and int(user_input) > 0:
        count = int(user_input)

    url = f"http://localhost:8080/api/benchmark?count={count}"
    print(f"\n[1/3] Invio richiesta HTTP GET a Spring Boot ({url})...")

    try:
        response = requests.get(url, timeout=60)
        if response.status_code != 200:
            print(f"ERRORE HTTP {response.status_code}: Impossibile completare il benchmark.")
            return
        data = response.json()
    except Exception as e:
        print(f"ERRORE CONNETTORE: Impossibile contattare Spring Boot su http://localhost:8080.")
        print(f"Assicurati che l'applicazione sia in esecuzione con './mvnw spring-boot:run'.")
        print(f"Dettagli errore: {e}")
        return

    lazy_time = data.get("lazyTimeMs", 0)
    lazy_queries = data.get("lazyQueries", 0)
    join_time = data.get("joinFetchTimeMs", 0)
    join_queries = data.get("joinFetchQueries", 0)
    graph_time = data.get("entityGraphTimeMs", 0)
    graph_queries = data.get("entityGraphQueries", 0)

    print("\n[2/3] RISULTATI DEL BENCHMARK:")
    print("+------------------------------------+----------------+----------------+")
    print("| STRATEGIA DI FETCHING              | TEMPO (ms)     | QUERY SQL      |")
    print("+------------------------------------+----------------+----------------+")
    print(f"| 1. LAZY Standard (N+1 Query)       | {lazy_time:14.2f} | {lazy_queries:14d} |")
    print(f"| 2. JPQL JOIN FETCH                 | {join_time:14.2f} | {join_queries:14d} |")
    print(f"| 3. @EntityGraph (Spring Data JPA)  | {graph_time:14.2f} | {graph_queries:14d} |")
    print("+------------------------------------+----------------+----------------+")

    print("\n[3/3] Apertura della finestra grafica con Matplotlib...")

    strategies = ['LAZY Standard', 'JPQL JOIN FETCH', '@EntityGraph']
    times = [lazy_time, join_time, graph_time]
    queries = [lazy_queries, join_queries, graph_queries]
    colors = ['#dc3545', '#0d6efd', '#198754']

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 6))
    fig.suptitle(f'FestivalProf - JPA Fetching Strategies Benchmark (Film Simulati: {count})', fontsize=16, fontweight='bold')

    bars1 = ax1.bar(strategies, times, color=colors, width=0.5)
    ax1.set_title('Tempo di Esecuzione (Millisecondi)', fontsize=12, fontweight='bold')
    ax1.set_ylabel('Tempo (ms)', fontsize=11)
    ax1.grid(axis='y', linestyle='--', alpha=0.7)

    for bar in bars1:
        yval = bar.get_height()
        ax1.text(bar.get_x() + bar.get_width()/2.0, yval + (yval * 0.02), f'{yval:.2f} ms', ha='center', va='bottom', fontweight='bold')

    bars2 = ax2.bar(strategies, queries, color=colors, width=0.5)
    ax2.set_title('Numero di Query SQL Eseguite', fontsize=12, fontweight='bold')
    ax2.set_ylabel('N° Query SQL', fontsize=11)
    ax2.grid(axis='y', linestyle='--', alpha=0.7)

    for bar in bars2:
        yval = bar.get_height()
        ax2.text(bar.get_x() + bar.get_width()/2.0, yval + 0.1, f'{int(yval)} SQL', ha='center', va='bottom', fontweight='bold')

    plt.tight_layout()
    plt.show()

def run_curve_benchmark():
    steps_input = input("\nInserisci gli step di N separati da virgola [Default: 20,40,60,80,100,150,200,300,400,500,750,1000,1500,2000]: ").strip()
    if not steps_input:
        steps_input = "20,40,60,80,100,150,200,300,400,500,750,1000,1500,2000"

    url = f"http://localhost:8080/api/benchmark/curve?steps={steps_input}"
    print(f"\n[1/3] Invio richiesta HTTP GET a Spring Boot per calcolare la curva d'andamento...")
    print(f"Query URL: {url}")

    try:
        response = requests.get(url, timeout=120)
        if response.status_code != 200:
            print(f"ERRORE HTTP {response.status_code}: Impossibile completare il benchmark a curva.")
            return
        data_list = response.json()
    except Exception as e:
        print(f"ERRORE CONNETTORE: Impossibile contattare Spring Boot su http://localhost:8080.")
        print(f"Dettagli errore: {e}")
        return

    n_values = [item.get("count") for item in data_list]
    lazy_times = [item.get("lazyTimeMs") for item in data_list]
    lazy_queries = [item.get("lazyQueries") for item in data_list]

    join_times = [item.get("joinFetchTimeMs") for item in data_list]
    join_queries = [item.get("joinFetchQueries") for item in data_list]

    graph_times = [item.get("entityGraphTimeMs") for item in data_list]
    graph_queries = [item.get("entityGraphQueries") for item in data_list]

    print("\n[2/3] RISULTATI DELLA CURVA D'ANDAMENTO:")
    print("+--------+-------------------+--------------------+--------------------+")
    print("| N      | LAZY Standard(ms) | JOIN FETCH (ms)    | @EntityGraph (ms)  |")
    print("+--------+-------------------+--------------------+--------------------+")
    for i in range(len(n_values)):
        print(f"| {n_values[i]:<6d} | {lazy_times[i]:17.2f} | {join_times[i]:18.2f} | {graph_times[i]:18.2f} |")
    print("+--------+-------------------+--------------------+--------------------+")

    print("\n[3/3] Generazione delle curve d'andamento con Matplotlib...")

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(15, 6))
    fig.suptitle('FestivalProf - Curva di Scalabilità JPA: Tempo di Esecuzione e Query SQL in funzione di N', fontsize=15, fontweight='bold')

    # Curva 1: Tempi di esecuzione (ms)
    ax1.plot(n_values, lazy_times, marker='o', linewidth=2.5, color='#dc3545', label='1. LAZY Standard (N+1)')
    ax1.plot(n_values, join_times, marker='s', linewidth=2.5, color='#0d6efd', label='2. JPQL JOIN FETCH (1 SQL)')
    ax1.plot(n_values, graph_times, marker='^', linewidth=2.5, color='#198754', label='3. @EntityGraph (1 SQL)')

    ax1.set_title('Andamento del Tempo di Esecuzione (ms) vs N Film', fontsize=12, fontweight='bold')
    ax1.set_xlabel('Numero di Film Simulati (N)', fontsize=11)
    ax1.set_ylabel('Tempo (Millisecondi)', fontsize=11)
    ax1.grid(True, linestyle='--', alpha=0.7)
    ax1.legend(loc='upper left')

    # Curva 2: Numero di Query SQL
    ax2.plot(n_values, lazy_queries, marker='o', linewidth=2.5, color='#dc3545', label='1. LAZY Standard (N+1 Query)')
    ax2.plot(n_values, join_queries, marker='s', linewidth=2.5, color='#0d6efd', label='2. JPQL JOIN FETCH (1 Query)')
    ax2.plot(n_values, graph_queries, marker='^', linewidth=2.5, color='#198754', label='3. @EntityGraph (1 Query)')

    ax2.set_title('Numero di Query SQL vs N Film', fontsize=12, fontweight='bold')
    ax2.set_xlabel('Numero di Film Simulati (N)', fontsize=11)
    ax2.set_ylabel('N° Query SQL', fontsize=11)
    ax2.grid(True, linestyle='--', alpha=0.7)
    ax2.legend(loc='upper left')

    plt.tight_layout()
    plt.show()

def main():
    print("=================================================================")
    print(" FESTIVALPROF - JPA/HIBERNATE FETCH STRATEGIES BENCHMARK (Python)")
    print("=================================================================")
    print("Seleziona la modalità di visualizzazione:")
    print("  [1] Benchmark Puntuale (Istogramma a barre per un singolo N)")
    print("  [2] Curva d'Andamento (Line Plot: Scalabilita' del tempo e delle query in funzione di N)")
    print("-----------------------------------------------------------------")
    
    choice = input("Scegli un'opzione [1/2, Default 2]: ").strip()
    if choice == '1':
        run_single_benchmark()
    else:
        run_curve_benchmark()

if __name__ == '__main__':
    main()
